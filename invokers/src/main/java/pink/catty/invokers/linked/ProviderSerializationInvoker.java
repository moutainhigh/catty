/*
 * Copyright 2019 The Catty Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pink.catty.invokers.linked;

import pink.catty.core.invoker.Invocation;
import pink.catty.core.invoker.Invocation.InvokerLinkTypeEnum;
import pink.catty.core.invoker.Invoker;
import pink.catty.core.invoker.AbstractLinkedInvoker;
import pink.catty.core.invoker.Request;
import pink.catty.core.invoker.Response;
import pink.catty.core.extension.spi.Serialization;
import pink.catty.core.service.MethodMeta;
import pink.catty.core.utils.AsyncUtils;
import pink.catty.core.utils.ExceptionUtils;
import java.util.concurrent.CompletionStage;

public class ProviderSerializationInvoker extends AbstractLinkedInvoker {

  private Serialization serialization;

  public ProviderSerializationInvoker(Invoker next, Serialization serialization) {
    super(next);
    if (serialization == null) {
      throw new NullPointerException("Serialization is null");
    }
    this.serialization = serialization;
  }

  @Override
  public Response invoke(Request request, Invocation invocation) {
    assert invocation.getLinkTypeEnum() == InvokerLinkTypeEnum.PROVIDER;

    Object[] args = request.getArgsValue();
    if (args != null) {
      MethodMeta methodMeta = invocation.getInvokedMethod();
      Class<?>[] parameterTypes = methodMeta.getMethod().getParameterTypes();
      Object[] afterDeserialize = new Object[args.length];
      for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof byte[]) {
          afterDeserialize[i] = serialization.deserialize((byte[]) args[i], parameterTypes[i]);
        } else {
          afterDeserialize[i] = args[i];
        }
      }
      request.setArgsValue(afterDeserialize);
    }

    Response response = next.invoke(request, invocation);
    MethodMeta methodMeta = invocation.getInvokedMethod();
    CompletionStage<Object> newResponse = response.thenApply(returnValue -> {
      if(returnValue instanceof Throwable
          && !Throwable.class.isAssignableFrom(methodMeta.getReturnType()) ) {
        // exception has been thrown.
        String exception = ExceptionUtils.toString((Throwable) returnValue);
        byte[] serialized = serialization.serialize(exception);
        byte[] finalBytes = new byte[serialized.length + 1];
        finalBytes[0] = 1; // exception has been thrown.
        System.arraycopy(serialized, 0, finalBytes, 1, serialized.length);
        return finalBytes;
      } else {
        byte[] serialized = serialization.serialize(returnValue);
        byte[] finalBytes = new byte[serialized.length + 1];
        finalBytes[0] = 0; // response status is ok.
        System.arraycopy(serialized, 0, finalBytes, 1, serialized.length);
        return finalBytes;
      }
    });
    return AsyncUtils.newResponse(newResponse, request.getRequestId());
  }

}

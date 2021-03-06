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
package pink.catty.invokers.endpoint;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import pink.catty.core.CattyException;
import pink.catty.core.extension.spi.Codec.DataTypeEnum;
import pink.catty.core.invoker.Response;

public class ClientChannelHandler extends ChannelDuplexHandler {

  private NettyClient nettyClient;

  public ClientChannelHandler(NettyClient nettyClient) {
    this.nettyClient = nettyClient;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    byte[] data = (byte[]) msg;
    Object object = nettyClient.getCodec().decode(data, DataTypeEnum.RESPONSE);
    if (!(object instanceof Response)) {
      throw new CattyException(
          "NettyChannelHandler: unsupported message type when encode: " + object
              .getClass());
    }
    processResponse((Response) object);
  }

  private void processResponse(Response response) {
    Response future = nettyClient.getResponseFuture(response.getRequestId());
    future.setValue(response.getValue());
  }

}

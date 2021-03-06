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
package pink.catty.example.extension;

import java.util.List;
import pink.catty.core.invoker.InvokerHolder;
import pink.catty.core.extension.spi.LoadBalance;
import pink.catty.extension.loadbalance.RandomLoadBalance;

public class MyLoadBalance implements LoadBalance {

  private RandomLoadBalance loadBalance = new RandomLoadBalance();

  @Override
  public InvokerHolder select(List<InvokerHolder> invokers) {
    System.out.println("my sl");
    return loadBalance.select(invokers);
  }
}

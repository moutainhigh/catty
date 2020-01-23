package io.catty.service;

import io.catty.annotation.Service;
import io.catty.utils.ReflectUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service meta info.
 *
 * Cache service's interface info to mark an easy entry.
 */
public class ServiceMeta {

  private Class<?> interfaceClass;

  private Object target;

  private Map<String, Method> methodMap;

  private Set<Method> validMethod;

  private Map<Method, MethodMeta> methodMetaMap;

  private String version = "";

  private String group = "";

  private String serviceName = "";

  public static ServiceMeta parse(Class<?> interfaceClass) {
    return new ServiceMeta(interfaceClass);
  }

  private ServiceMeta(Class<?> interfaceClass) {
    this.interfaceClass = interfaceClass;
    methodMap = new HashMap<>();
    validMethod = new HashSet<>();
    methodMetaMap = new HashMap<>();

    List<Method> methods = ReflectUtils.getPublicMethod(interfaceClass);
    for (Method method : methods) {
      String methodSign = ReflectUtils.getMethodSign(method);
      if(methodMap.containsKey(methodSign)) {
        throw new Error("Duplicated method sign: " + methodSign + "#" + method
            + ". Method's signature excepted unique.");
      }
      methodMap.put(methodSign, method);
      validMethod.add(method);
      methodMetaMap.put(method, new MethodMeta(method));
    }

    if(interfaceClass.isAnnotationPresent(Service.class)) {
      Service serviceInfo = interfaceClass.getAnnotation(Service.class);
      this.version = serviceInfo.version();
      this.group = serviceInfo.group();
      this.serviceName = serviceInfo.name();
    }
  }

  public Class<?> getInterfaceClass() {
    return interfaceClass;
  }

  public Set<Method> getValidMethod() {
    return validMethod;
  }

  public MethodMeta getMethodMeta(Method method) {
    return methodMetaMap.get(method);
  }

  public void setTarget(Object target) {
    this.target = target;
  }

  public Object getTarget() {
    return target;
  }

  public String getVersion() {
    return version;
  }

  public String getGroup() {
    return group;
  }

  public String getServiceName() {
    return serviceName;
  }

  public MethodMeta getMethodMetaBySign(String methodSign) {
    Method method = methodMap.get(methodSign);
    if(method == null) {
      return null;
    } else {
      return methodMetaMap.get(method);
    }
  }

  public Method getMethodBySign(String methodSign) {
    return methodMap.get(methodSign);
  }

}
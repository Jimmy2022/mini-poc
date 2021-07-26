/**
 * Copyright:Copyright (c) 2021
 * Create By Jimmy
 * Create On 2021-21.7.12 at 1:04
 **/
package agent;

import javassist.*;
import javassist.bytecode.Descriptor;
import javassist.bytecode.stackmap.TypeData;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SimpleTransformer implements ClassFileTransformer {
    public static final String TARGET_NAME = "target.HttpExample";

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        System.out.println("load class:" + className);
        if (className.equals(Descriptor.toJvmName(TARGET_NAME))) {
            ClassPool pool = ClassPool.getDefault();
            try {
                CtClass clazz = pool.get(TARGET_NAME);
                CtMethod method = clazz.getMethod("query",
                        Descriptor.ofMethod(CtClass.voidType,
                                new CtClass[]{pool.get("java.lang.String")}));
                System.out.println("methodInfo.getName():" + method.getMethodInfo().getName());
                System.out.println("methodInfo.getConstPool():" + method.getMethodInfo().getConstPool());


                // write code string is difficult, so call method here
                method.insertBefore("agent.Code.before($1);");
                method.insertAfter("agent.Code.after();");
                method.insertAfter("System.out.println($0.name);");

                passValue(pool, method);

                System.out.println("is class frozen before toBytecode:" + clazz.isFrozen());
                byte[] clazzBytes = clazz.toBytecode();
                clazz.detach();
                System.out.println("is class frozen after toBytecode:" + clazz.isFrozen());
                clazz.freeze();
                return clazzBytes;
            } catch (NotFoundException | IOException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }


    /**
     * if just insert declaration
     * <code>
     *     method.insertAfter("org.json.JSONObject p = new org.json.JSONObject(\"" + insert + "\");");
     *     method.insertAfter("System.out.println(p);");
     *     </>
     * will throw exception when run target class:javassist.CannotCompileException: [source error] no such field: p
     *
     * if both insert addLocalVariable and declaration
     * <code>
     *     method.addLocalVariable("p", pool.getCtClass("org.json.JSONObject"));
     *     method.insertAfter("org.json.JSONObject p = new org.json.JSONObject(\"" + insert + "\");");
     * </code>
     * will throw as fellow:
     * <p>
     *     Caused by: javassist.bytecode.BadBytecode: query (Ljava/lang/String;)V in target.HttpExample: conflict: *top* and java.lang.Object
     *      	at javassist.bytecode.stackmap.MapMaker.make(MapMaker.java:119)
     *      	at javassist.bytecode.MethodInfo.rebuildStackMap(MethodInfo.java:458)
     * 	        at javassist.bytecode.MethodInfo.rebuildStackMapIf6(MethodInfo.java:440)
     * 	        at javassist.CtBehavior.insertAfter(CtBehavior.java:964)
     * 	... 18 more
     * Caused by: javassist.bytecode.BadBytecode: conflict: *top* and java.lang.Object
     * </>
     * it seems the wrong "method stack",  {@link TypeData.BasicType} does not need setType, but it was called,
     * somewhere may not aligned, its name is java.lang.Object but a BasicType, it's impossible
     *
     * finally, I pass the Map type data to the target code successfully, by serialize variable to String
     * @param pool
     * @param method
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void passValue(ClassPool pool, CtMethod method) throws NotFoundException, CannotCompileException {
        JSONObject obj = new JSONObject();
        obj.put("key1", 1);
        obj.put("key2", 2);
        String insert = obj.toString().replace("\"", "\\\"");
        method.addLocalVariable("p", pool.getCtClass("org.json.JSONObject"));


        method.insertAfter("System.out.println(\"" + insert + "\");");
        method.insertAfter("p = new org.json.JSONObject(\"" + insert + "\");");
        method.insertAfter("System.out.println(p);");
        method.insertAfter("System.out.println(p.getInt(\"key1\"));");

    }
}

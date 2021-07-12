/**
 * Copyright:Copyright (c) 2021
 * Create By Jimmy
 * Create On 2021-21.7.12 at 1:04
 **/
package agent;

import javassist.*;
import javassist.bytecode.Descriptor;

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
}

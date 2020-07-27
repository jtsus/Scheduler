package net.eterniamc.scheduler;

import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import lombok.SneakyThrows;
import net.eterniamc.scheduler.annotation.Delayed;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ClassImpactor {
    private static final String SOURCE_FORMAT =
            "public void %s(%s) {" +
                "Object[] params = new Object[%s];" +
                "%s" +
                "net.eterniamc.scheduler.SchedulerController.INSTANCE.delay(%sL,%s,\"%s\",%s,\"%s\",params);" +
            "}";

    public static boolean writeClasses = true;

    @SneakyThrows
    public static void register(String toLoad) {
        CtClass ctClass = ClassPool.getDefault().getCtClass(toLoad);
        boolean didSomething = false;
        for (CtMethod method : getAllMethods(ctClass.getDeclaredMethods())) {
            didSomething = impact(ctClass, method) || didSomething;
        }
        if (didSomething) {
            ctClass.toClass(ClassImpactor.class.getClassLoader());
            if (writeClasses) {
                ctClass.writeFile();
            }
        }
    }

    @SneakyThrows
    public static boolean impact(CtClass ctClass, CtMethod method) {
        AnnotationData data = new AnnotationData((Delayed) method.getAnnotation(Delayed.class));
        long rate = data.getTime();
        boolean async = data.isAsync();

        if (rate > 0) {
            String name = method.getName();
            // There may be a better name substitution to do here, unsure
            method.setName("__" + name + "$original");
            String signature = method.getSignature();
            // Param types must be obtained in order to ensure a primitive isn't changed to an Object
            List<String> paramTypes = getTypeNamesFromSignature(signature);
            StringJoiner joiner = new StringJoiner(",");
            for (int i1 = 0; i1 < paramTypes.size(); i1++) {
                String name1 = paramTypes.get(i1);
                String s = name1 + " var" + i1;
                joiner.add(s);
            }
            String params = joiner.toString();
            StringBuilder paramCreation = new StringBuilder();
            // Top of stack in non-static method is "this" instead of first parameter
            int isStatic = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
            for (int i1 = 0; i1 < paramTypes.size(); i1++) {
                paramCreation.append("params[").append(i1).append("]=").append(getCastFor(paramTypes.get(i1), "$" + (i1 + isStatic))).append(";");
            }
            String selfReference = isStatic == 0 ? "null" : "this";
            String source = String.format(
                    SOURCE_FORMAT,
                    name,
                    params,
                    paramTypes.size(),
                    paramCreation,
                    rate,
                    async,
                    ctClass.getClassFile().getName(),
                    selfReference,
                    method.getName()
            );
            CtMethod generated = CtMethod.make(source, ctClass);
            // Make method static if original method is
            generated.setModifiers(method.getModifiers());
            // This can not be included in source due to obfuscation issues
            generated.getMethodInfo().setDescriptor(signature);
            ctClass.addMethod(generated);
            return true;
        }
        return false;
    }

    @SneakyThrows
    public static CtMethod[] getAllMethods(CtMethod[] methods) {
        List<CtMethod> methods1 = new ArrayList<>();
        for (CtMethod method : methods) {
            if (method.hasAnnotation(Delayed.class) && method.getReturnType() == CtClass.voidType) {
                methods1.add(method);
            }
        }
        return methods1.toArray(new CtMethod[0]);
    }

    public static List<String> getTypeNamesFromSignature(String signature) {
        List<String> params = Lists.newArrayList();
        signature = signature.substring(1, signature.indexOf(')'));
        for (int i = 0; i < signature.length(); i++) {
            String name;
            if (signature.charAt(i) == 'L' ) {
                int end = signature.substring(i).indexOf(';') + i;
                name = getTypeNameFromASMName(signature.substring(i + 1, end));
                i = end;
            } else if (signature.charAt(i) == '[') {
                int j = signature.substring(i).lastIndexOf('[') + 1;
                int end;
                if (signature.charAt(j) == 'L') {
                    end = signature.substring(i).indexOf(';') + i;
                } else {
                    end = j;
                }
                name = getTypeNameFromASMName(signature.substring(i, end + 1));
                i = end;
            } else {
                name = getTypeNameFromASMName(signature.substring(i, i + 1));
            }
            params.add(name);
        }
        return params;
    }

    public static String getTypeNameFromASMName(String name) {
        switch (name) {
            case "B":
                return byte.class.getName();
            case "C":
                return char.class.getName();
            case "D":
                return double.class.getName();
            case "F":
                return float.class.getName();
            case "I":
                return int.class.getName();
            case "J":
                return long.class.getName();
            case "S":
                return short.class.getName();
            case "Z":
                return boolean.class.getName();
            case "V":
                return void.class.getName();
        }
        if (name.startsWith("[")) {
            return getTypeNameFromASMName(name.replaceAll("[L;]", "").substring(1)) + "[]";
        }
        return "java.lang.Object";
    }

    public static String getCastFor(String type, String code) {
        if (type.equals("int")) return "new Integer(" + code + ")";
        if (type.equals("float")) return "new Float(" + code + ")";
        if (type.equals("byte")) return "new Byte(" + code + ")";
        if (type.equals("double")) return "new Double(" + code + ")";
        if (type.equals("long")) return "new Long(" + code + ")";
        if (type.equals("char")) return "new Character(" + code + ")";
        if (type.equals("boolean")) return "new Boolean(" + code + ")";
        if (type.equals("short")) return "new Short(" + code + ")";
        return code;
    }
}

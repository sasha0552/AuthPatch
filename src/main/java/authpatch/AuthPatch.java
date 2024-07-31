package authpatch;

import authpatch.transformer.BaseClassTransformer;
import authpatch.transformer.ConstantPoolClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AuthPatch implements ClassFileTransformer {
    private static byte[] classTransformer(byte[] classfileBuffer, BaseClassTransformer classTransformer) {
        // Create a ClassReader to read the bytecode of the class
        ClassReader classReader = new ClassReader(classfileBuffer);

        // Create a ClassWriter to write the transformed bytecode
        ClassWriter classWriter = new ClassWriter(classReader, 0);

        // Set the ClassWriter as the ClassVisitor for the transformer
        classTransformer.setClassVisitor(classWriter);

        // Accept the class and apply transformations
        classReader.accept(classTransformer, 0);

        // Return the transformed bytecode as a byte array
        return classWriter.toByteArray();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        // Add this ClassFileTransformer to the instrumentation instance
        inst.addTransformer(new AuthPatch());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // Check if the class belongs to the authlib package
        if (className.startsWith("com/mojang/authlib/")) {
            // Transform the class using the ConstantPoolClassTransformer
            return classTransformer(classfileBuffer,
                    new ConstantPoolClassTransformer((value) -> {
                        // Apply transformations to string constants in the constant pool
                        if (value instanceof String) {
                            // Cast the value to a string
                            String string = (String) value;

                            // Replace ".minecraft.net" with the value from the system property
                            if (string.equals(".minecraft.net")) {
                                return System.getProperty("authpatch.texture_whitelist_1", "<property missing>");
                            }

                            // Replace ".mojang.com" with the value from the system property
                            if (string.equals(".mojang.com")) {
                                return System.getProperty("authpatch.texture_whitelist_2", "<property missing>");
                            }

                            // Replace specific URLs with the corresponding values from system properties
                            return string
                                    .replace("https://api.minecraftservices.com", System.getProperty("authpatch.api_base", "<property missing>"))
                                    .replace("https://api.mojang.com", System.getProperty("authpatch.api_base", "<property missing>"))
                                    .replace("https://authserver.mojang.com", System.getProperty("authpatch.auth_base", "<property missing>"))
                                    .replace("https://sessionserver.mojang.com", System.getProperty("authpatch.session_base", "<property missing>"));
                        }

                        // Return the original value if no transformation is applied
                        return value;
                    })
            );
        }

        // Return null if no transformations are applied to the class
        return null;
    }
}

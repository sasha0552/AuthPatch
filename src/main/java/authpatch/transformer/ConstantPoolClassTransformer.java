package authpatch.transformer;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Function;

public class ConstantPoolClassTransformer extends BaseClassTransformer {
    // A function that transforms constants in the constant pool
    private final Function<Object, Object> function;

    public ConstantPoolClassTransformer(Function<Object, Object> function) {
        // Initialize the BaseClassTransformer with ASM version 9
        super(Opcodes.ASM9);

        // Assign the function to the class field
        this.function = function;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // Apply the transformation function to the field's initial value
        value = function.apply(value);

        // Visit the field with the potentially transformed value
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // Get the default MethodVisitor
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);

        // Return a new MethodVisitor that overrides the visitLdcInsn method
        return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
            @Override
            public void visitLdcInsn(Object value) {
                // Apply the transformation function to the constant value
                value = function.apply(value);

                // Visit the LDC instruction with the potentially transformed value
                super.visitLdcInsn(value);
            }
        };
    }
}

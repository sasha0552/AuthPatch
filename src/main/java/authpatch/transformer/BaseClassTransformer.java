package authpatch.transformer;

import org.objectweb.asm.ClassVisitor;

public abstract class BaseClassTransformer extends ClassVisitor {
    public BaseClassTransformer(int api) {
        // Initialize the ClassVisitor with the specified ASM API version
        super(api);
    }

    public BaseClassTransformer(int api, ClassVisitor classVisitor) {
        // Initialize the ClassVisitor with the specified ASM API version and delegate ClassVisitor
        super(api, classVisitor);
    }

    /**
     * Sets the delegate ClassVisitor.
     *
     * @param classVisitor the new ClassVisitor to delegate to
     */
    public void setClassVisitor(ClassVisitor classVisitor) {
        // Set the delegate ClassVisitor
        this.cv = classVisitor;
    }
}

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.nio.file.*;
public class Patch {
  public static void main(String[] a) throws Exception {
    byte[] b = Files.readAllBytes(Path.of(a[0]));
    ClassNode cn = new ClassNode(); new ClassReader(b).accept(cn, 0);
    int n = 0;
    for (MethodNode m : cn.methods) if (m.name.equals("clientEP") && m.desc.equals("()V")) {
      for (AbstractInsnNode i : m.instructions.toArray()) if (i.getOpcode() == Opcodes.RETURN) {
        m.instructions.insertBefore(i, new MethodInsnNode(Opcodes.INVOKESTATIC, "dev/kikugie/elytratrims/compat/CovalentIrisFix", "register", "()V", false)); n++;
      }
    }
    if (n == 0) throw new IllegalStateException("clientEP RETURN not found");
    ClassWriter cw = new ClassWriter(cn.version < 50 ? 0 : 0); // no frame recompute needed: call inserted before RETURN, stack empty
    cn.accept(cw);
    Files.write(Path.of(a[1]), cw.toByteArray());
    System.out.println("patched returns: " + n);
  }
}

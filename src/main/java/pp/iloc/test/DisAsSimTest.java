package pp.iloc.test;

import org.junit.Test;
import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.eval.Machine;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;
import sun.misc.VM;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Jens on 17-5-2016.
 *
 */
public class DisAsSimTest {

    @Test
    public void test() {
        testAssembler("max");
//        testDisassembler("max");
        testSimulator("max", "max");
        testAssembler("fib-r-to-r");
        testSimulator("fib-r-to-r", "fibr");
        testSimulator("fib-m-to-m", "fibm");
        compareSimulator("fib-r-to-r", "fib-m-to-m");
        testSimulator("fib-r-to-r", "of");
    }

    public void compareSimulator(String file1, String file2) {
        Program p1 = parse(file1);
        Machine c1 = new Machine();
        Program p2 = parse(file2);
        Machine c2 = new Machine();
        c1.setNum("n", 9);
        new Simulator(p1, c1).run();
        c2.setNum("n", 9);
        new Simulator(p2, c2).run();
        assertEquals(c1.getReg("r_z"), c2.getReg("r_z"));
    }

    public void testAssembler(String filename) {
        Program p = parse(filename);
        System.out.println(p.prettyPrint());
    }

//    public void testDisassembler(String filename) {
//    }


    public void testSimulator(String filename, String test) {
        Program p = parse(filename);
        Machine c = new Machine();
        if (test.equals("max")) {
            c.setNum("alength", 5);
            c.init("a", 3, 4, 2, 5, 6);
            Simulator s = new Simulator(p, c);
            s.run();
            assertEquals(6, c.getReg("r_max"));
        } else if (test.equals("fibr")) {
            c.setNum("n", 9);
            new Simulator(p, c).run();
            assertEquals(55, c.getReg("r_z"));
        } else if (test.equals("fibm")) {
            c.setNum("n", 9);
            new Simulator(p, c).run();
            assertEquals(55, c.getReg("r_z"));
        } else if (test.equals("of")) {
            c.setNum("n", 45);
            new Simulator(p, c).run();
            assertTrue(0 < c.getReg("r_z"));
            System.out.println("Value of n: " + 45);
            System.out.println("Fib: " + c.getReg("r_z"));
            c = new Machine();
            c.setNum("n", 48);
            new Simulator(p, c).run();
            assertFalse(0 < c.getReg("r_z"));
            System.out.println("Value of n: " + 48);
            System.out.println("Fib: " + c.getReg("r_z"));
        }
    }

    Program parse(String filename) {
        File file = new File(filename + ".iloc");
        if (!file.exists()) {
            file = new File(BASE_DIR + filename + ".iloc");
        }
        try {
            Program result = Assembler.instance().assemble(file);
            String print = result.prettyPrint();
            Program other = Assembler.instance().assemble(print);
            assertEquals(result, other);
            return result;
        } catch (FormatException | IOException e) {
            fail(e.getMessage());
            return null;
        }
    }
    private final static String BASE_DIR = "pp/iloc/sample/";

}

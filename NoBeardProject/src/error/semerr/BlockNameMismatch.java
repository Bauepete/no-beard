/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package error.semerr;

/**
 *
 * @author peter
 */
public class BlockNameMismatch extends SemErr {

    public BlockNameMismatch(String name1, String name2) {
        super("Block " + name1 + " ends with name " + name2, 50);
    }
}

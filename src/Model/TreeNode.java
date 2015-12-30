package Model;

/**
 * Created by Joel on 5/5/2015.
 */
public class TreeNode
{
    public int wins=0;
    public int visits=0;
    public int depth;
    public int x, y; // position of move
    public TreeNode parent; //optional
    public TreeNode child;
    public TreeNode sibling;
    public TreeNode(TreeNode parent, int x, int y, int depth) {
        this.parent = parent;
        this.x=x;
        this.y=y;
        this.depth = depth;
    }
    public void update(int val) {
        visits++;
        wins+=val;
    }
    public double getWinRate() {
        if (visits>0) return (double)wins / visits;
        else return 0; // should not happen
    }
}

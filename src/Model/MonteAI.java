package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Joel on 5/5/2015.
 */
public class MonteAI extends AI
{

    public TreeNode root;
    public static final double UCTK = Math.sqrt(1.0/5);
    public final int NUM_SIMULATIONS = 1;

    public MonteAI(BoardModel bm, BoardModel.Stone color)
    {
        super(bm,color);
        root = null;
    }

    public Point genMove()
    {
        return UCTSearch(NUM_SIMULATIONS);
    }

    // child with highest number of visits is used (not: best winrate)
    public TreeNode getBestChild(TreeNode root) {
        TreeNode child = root.child;
        TreeNode best_child = null;
        int  best_visits= -1;
        while (child!=null) { // for all children
            if (child.visits>best_visits) {
                best_child=child;
                best_visits=child.visits;
            }
            child = child.sibling;
        }
        return best_child;
    }

    public TreeNode UCTSelect(TreeNode node) {
        TreeNode res=null;
        TreeNode next = node.child;
        double best_uct=0;
        while (next!=null) { // for all children
            double uctvalue;
            if (next.visits > 0) {
                double winrate=next.getWinRate();
                double uct = UCTK * Math.sqrt( Math.log(node.visits) / next.visits );
                uctvalue = winrate + uct;
            }
            else {
                // Always play a random unexplored move first
                uctvalue = 10000 + 1000*Math.random();
            }
            if (uctvalue > best_uct) { // get max uctvalue of all children
                best_uct = uctvalue;
                res = next;
            }
            next = next.sibling;
        }
        return res;
    }


    public Point getRandomMoveHeuristic(BoardModel clone, BoardModel.Stone color)
    {
        List<Point> legal = clone.getLegalMoves(color);
        Random rng = new Random(System.currentTimeMillis());

        List<Point> toBeRemoved = new ArrayList<>();
        for(Point l : legal)
        {
            if(clone.isEye(color,l.getX(),l.getY()))
                toBeRemoved.add(l);
        }
        for(Point t : toBeRemoved)
            legal.remove(t);

        if(legal.size() > 0) {
            // does not need to keep track of captures
            return legal.get(rng.nextInt(legal.size()));
        }
        else
            return null;
    }

    // plays a random game and return 0 for loss and 1 for win from a given position in clone
    public int playRandomGame(BoardModel clone)
    {
        BoardModel.Stone opColor = (color == BoardModel.Stone.BLACK)? BoardModel.Stone.WHITE: BoardModel.Stone.BLACK;
        boolean p1Pass = false,p2Pass = false;
        while(!p1Pass && !p2Pass)
        {
            p1Pass = false;
            p2Pass = false;
            Point p1Move = getRandomMoveHeuristic(clone,color);
            if(p1Move == null)
                p1Pass = true;
            else
                clone.playMove(color,p1Move.getX(),p1Move.getY());

            Point p2Move = getRandomMoveHeuristic(clone,opColor);
            if(p2Move == null)
                p2Pass = true;
            else
                clone.playMove(opColor,p2Move.getX(),p2Move.getY());
        }
        clone.removeDeadStones(color);
        clone.removeDeadStones(opColor);
        int p1Points = clone.scoreBoard(color);
        int p2Points = clone.scoreBoard(opColor);
        return (p1Points > p2Points)?1:0;
    }

    // return 0=lose 1=win for current player to move
    public int playSimulation(BoardModel clone, TreeNode n)
    {
        int randomresult;
        if (n.child==null && n.visits<NUM_SIMULATIONS) { // 10 simulations until chilren are expanded (saves memory)
            randomresult = playRandomGame(clone);
        }
        else {
            if (n.child == null)
                createChildren(clone, n);
            TreeNode next = UCTSelect(n); // select a move
            if (next==null) { /* ERROR */ }
            clone.playMove(color,next.x, next.y);
            int res=playSimulation(clone,next);
            randomresult = 1-res;
        }
        n.update(1-randomresult); //update node (Node-wins are associated with moves in the Nodes)
        return randomresult;
    }

    public void createChildren(BoardModel model, TreeNode node)
    {
        List<Point> moves = model.getLegalMoves(color);
        for(Point m : moves)
        {
            if(node.child == null)
                node.child = new TreeNode(node,m.getX(),m.getY(),node.depth + 1);
            else{
                TreeNode newNode = new TreeNode(node,m.getX(),m.getY(), node.depth + 1);
                newNode.sibling = node.child;
                node.child = newNode;
            }

        }

    }

    // generate a move, using the uct algorithm
    public Point UCTSearch(int numsim) {
        root=new TreeNode(null,-1,-1, 0); //init uct tree
        createChildren(bm, root);
        for (int i=0; i<numsim; i++) {
            BoardModel clone = new BoardModel(bm);
            playSimulation(clone, root);
        }
        TreeNode n=getBestChild(root);
        return new Point(n.x, n.y);
    }
}

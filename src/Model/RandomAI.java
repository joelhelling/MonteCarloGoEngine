package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Joel on 5/3/2015.
 */
public class RandomAI extends AI {

    public RandomAI(BoardModel bm, BoardModel.Stone color)
    {
        super(bm,color);
    }

    public Point genMove()
    {
        List<Point> legal = bm.getLegalMoves(color);
        Random rng = new Random(System.currentTimeMillis());

        List<Point> toBeRemoved = new ArrayList<>();
        for(Point l : legal)
        {
            if(bm.isEye(color,l.getX(),l.getY()))
                toBeRemoved.add(l);
        }
        for(Point t : toBeRemoved)
            legal.remove(t);

        if(legal.size() > 0) {
            Point move = legal.get(rng.nextInt(legal.size()));
            // does not need to keep track of captures
            bm.playMove(color,move.getX(),move.getY());
            return move;
        }
        else
            return null;
    }
}

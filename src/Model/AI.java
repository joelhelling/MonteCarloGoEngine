package Model;

/**
 * Created by Joel on 5/3/2015.
 */
public abstract class AI
{
    protected BoardModel bm;
    protected BoardModel.Stone color;


    public AI(BoardModel bm,BoardModel.Stone color)
    {
        this.color = color;
        this.bm = bm;
    }

    // should be called before genMove
    public void updateModel(String record)
    {
        bm.updateModel(record);
    }

    public void resetBoard()
    {
        bm.reset();
    }

    // return either a Point for the stone to move or null for a pass
    public abstract Point genMove();
}

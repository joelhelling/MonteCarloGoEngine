package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * GoEngine
 * Created by Joel on 4/5/2015.
 */
public class BoardModel
{
    private List<String> moveHistory;
    private Stone[][] board;
    public enum Stone {EMPTY, BLACK, WHITE}
    public static final int BOARD_SIZE = 9;
    private int blackCaptured;
    private int whiteCaptured;

    public BoardModel()
    {
        this.blackCaptured = 0;
        this.whiteCaptured = 0;
        this.board = genStartingBoard();
        moveHistory = new ArrayList<>(BOARD_SIZE*BOARD_SIZE);
        moveHistory.add(recordTurn(this.board));
    }

    public BoardModel(BoardModel orig)
    {
        this.blackCaptured = orig.blackCaptured;
        this.whiteCaptured = orig.whiteCaptured;
        this.board = orig.copyModel();
        this.moveHistory = orig.getMoveHistory();
    }

    public Stone[][] getBoard()
    {
        return this.board;
    }

    public int getHeight()
    {
        return BOARD_SIZE;
    }

    public int getWidth()
    {
        return BOARD_SIZE;
    }

    public List<String> getMoveHistory()
    {
        List<String> history = new ArrayList<>();
        for(String m : this.moveHistory)
            history.add(m);
        return history;

    }

    public int getBlackCaptured()
    {
        return blackCaptured;
    }

    public int getWhiteCaptured()
    {
        return whiteCaptured;
    }

    public void setBlackCaptured(int captured)
    {
        blackCaptured = captured;
    }

    public void setWhiteCaptured(int captured)
    {
        whiteCaptured = captured;
    }

    // returns the number of turns in the move history except the empty starting board.
    public int getTurn()
    {
        return moveHistory.size()-1;
    }

    public void pass()
    {
        moveHistory.add(getCurrentTurn());
    }

    // returns number of captured stones
    public int playMove(Stone color, int x, int y)
    {
        List<Point> legal = getLegalMoves(color);
        Stone opColor = (color == Stone.BLACK)?Stone.WHITE:Stone.BLACK;
        if(legal.contains(new Point(x,y)))
        {
            this.board[x][y] = color;

            List<Point> neighbors = getNeighbors(x,y);
            int captures = 0;
            for(Point n : neighbors)
            {
                if(countLiberties(opColor,n.getX(),n.getY()) == 0)
                    captures += resolveCapture(opColor,n.getX(),n.getY());
            }
            moveHistory.add(recordTurn(this.board));
            return captures;
        }
        return 0;
    }


    public void addStone(Stone color, int x, int y)
    {
        board[x][y] = color;
    }

    public List<Point> getLegalMoves(Stone color)
    {
        List<Point> legal = new ArrayList<>();
        List<Point> candidates = getEmptyIntersections();

        for(Point c : candidates)
        {
            if(isLegal(color,c.getX(),c.getY()))
                legal.add(c);
        }

        return legal;
    }


    public boolean isLegal(Stone color, int x, int y)
    {
        // Just in case called from somewhere besides getLegalMoves
        if(board[x][y] != Stone.EMPTY)
            return false;

        List<Point> neighbors = getNeighbors(x,y);
        Stone opColor = (color == Stone.WHITE)?Stone.BLACK:Stone.WHITE;

        board[x][y] = color;
        // Ko rule
        if(moveHistory.size() > 2 && recordTurn(board).equals(getTurn(moveHistory.size()-2))) {
            board[x][y] = Stone.EMPTY;
            return false;
        }

        if(countLiberties(color,x,y) > 0) {
            board[x][y] = Stone.EMPTY;
            return true;
        }

        for(Point c : neighbors)
        {
            if(countLiberties(opColor,c.getX(),c.getY()) <= 0) {
                board[x][y] = Stone.EMPTY;
                return true;
            }
        }
        board[x][y] = Stone.EMPTY;
        return false;
    }

    // counts liberties of group with color: color
    public int countLiberties( Stone color, int x, int y)
    {
        if(board[x][y] == Stone.EMPTY)
            return -1;

        List<Point> explored = new ArrayList<>();
        List<Point> libs = new ArrayList<>();
        List<Point> next = new ArrayList<>();
        next.add(new Point(x,y));
        while(!next.isEmpty())
        {
            Point current = next.remove(next.size()-1);
            explored.add(current);
            List<Point> neighbors = getNeighbors(current.getX(),current.getY());
            for(Point n : neighbors)
            {
                if(board[n.getX()][n.getY()] == Stone.EMPTY && !libs.contains(n))
                {
                    libs.add(n);
                }
                else if(board[n.getX()][n.getY()] == color && !explored.contains(n))
                {
                    next.add(n);
                }
            }
        }
        return libs.size();
    }

    public List<Point> getLiberties(Stone color, int x, int y)
    {
        if(board[x][y] == Stone.EMPTY)
            return null;

        List<Point> explored = new ArrayList<>();
        List<Point> libs = new ArrayList<>();
        List<Point> next = new ArrayList<>();
        next.add(new Point(x,y));
        while(!next.isEmpty())
        {
            Point current = next.remove(next.size()-1);
            explored.add(current);
            List<Point> neighbors = getNeighbors(current.getX(),current.getY());
            for(Point n : neighbors)
            {
                if(board[n.getX()][n.getY()] == Stone.EMPTY && !libs.contains(n))
                {
                    libs.add(n);
                }
                else if(board[n.getX()][n.getY()] == color && !explored.contains(n))
                {
                    next.add(n);
                }
            }
        }
        return libs;
    }

    // chinese counting rules
    // assumes that all dead stones are removed and each eye is only one intersection
    public int scoreBoard(Stone color)
    {
        int count = 0;
        Stone opColor = (color == Stone.BLACK)?Stone.WHITE:Stone.BLACK;
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                if(board[i][j] == color)
                    count += 1;

        List<Point> explored = new ArrayList<>();
        List<Point> empty = getEmptyIntersections();
        for(Point pt : empty)
        {
            // Assumes that area will be surrounded by one color
            List<Point> area = new ArrayList<>();
            List<Point> next = new ArrayList<>();
            next.add(pt);
            boolean rightColor = true;
            while(!next.isEmpty() && rightColor)
            {
                Point current = next.remove(next.size() - 1);
                area.add(current);
                explored.add(current);
                List<Point> neighbors = getNeighbors(current.getX(), current.getY());
                for(Point n : neighbors)
                {
                    if(board[n.getX()][n.getY()] == Stone.EMPTY && !explored.contains(n))
                    {
                        next.add(n);
                    }
                    else if(board[n.getX()][n.getY()] == opColor)
                    {
                        rightColor = false;
                    }

                }
            }
            if(rightColor)
                count += area.size();
        }

        return count;
    }

//    public int scoreBoard(Stone color)
//    {
//        List<Point> potentialTerritory = getEmptyIntersections();
//        List<Point> counted = new ArrayList<>();
//        for(Point pt : potentialTerritory)
//        {
//            if(!counted.contains(pt))
//            {
//                List<Point> neighbors = getNeighbors(pt.getX(), pt.getY());
//                // must have at least 1 neighbor
//                Point check = neighbors.get(0);
//                if (board[check.getX()][check.getY()] == color)
//                {
//                    List<Point> next = new ArrayList<>();
//                    next.add(pt);
//                    while(!next.isEmpty())
//                    {
//                        Point current = next.remove(next.size()-1);
//                        List<Point> reNeighbors = getNeighbors(current.getX(),current.getY());
//                        counted.add(current);
//                        for(Point n : reNeighbors)
//                        {
//                            if((board[n.getX()][n.getY()] == Stone.EMPTY || board[n.getX()][n.getY()] == color)
//                                && !counted.contains(n))
//                            {
//                                next.add(n);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return counted.size();
//    }

    public int removeDeadStones(Stone color)
    {
        int removed = 0;
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] == color)
                {
                    List<Point> libs = getLiberties(color,i,j);
                    int eyeCount = 0;
                    for(Point l : libs)
                    {
                        if(isEye(color,l.getX(),l.getY()))
                        {
                            eyeCount += 1;
                        }
                    }
                    // Alive groups must have 2 eyes
                    if(eyeCount < 2)
                    {
                        removed += resolveCapture(color,i,j);
                    }
                }
            }
        }
        return removed;
    }

    public boolean isEye(Stone color, int x, int y)
    {
        List<Point> neighbors = getNeighbors(x,y);
        List<Point> diags = getDiagNeighbors(x,y);

        for(Point n : neighbors)
        {
            if(board[n.getX()][n.getY()] != color)
                return false;
        }

        // detect false eyes
        int falseCount = 0;
        // if the eye is on the wall
        if(diags.size() < 4)
            falseCount += 1;

        Stone opColor = (color == Stone.BLACK)?Stone.WHITE:Stone.BLACK;
        for(Point d : diags)
        {
            if(board[d.getX()][d.getY()] == opColor)
                falseCount += 1;
        }

        return falseCount < 2;

    }

    public List<Point> getEmptyIntersections()
    {
        List<Point> empty = new ArrayList<>();
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] == Stone.EMPTY)
                    empty.add(new Point(i,j));
            }
        }
        return empty;
    }

    // captures the group of color: color
    public int resolveCapture(Stone color, int x, int y)
    {
        if(board[x][y] == Stone.EMPTY || board[x][y] != color)
            return 0;

        List<Point> next = new ArrayList<>();
        next.add(new Point(x,y));
        int captured = 0;
        while(!next.isEmpty())
        {
            Point c = next.remove(next.size()-1);
            removeStone(c.getX(),c.getY());
            captured++;
            List<Point> neighbors = getNeighbors(c.getX(),c.getY());
            for(Point n : neighbors)
            {
                if(color == board[n.getX()][n.getY()])
                {
                    next.add(n);
                }
            }

        }
        return captured;
    }

    public List<Point> getNeighbors(int x, int y)
    {
        List<Point> neighbors = new ArrayList<>();

        if(!outOfBounds(x-1,y))
            neighbors.add(new Point(x-1,y));
        if(!outOfBounds(x+1,y))
            neighbors.add(new Point(x+1,y));
        if(!outOfBounds(x,y-1))
            neighbors.add(new Point(x,y-1));
        if(!outOfBounds(x,y+1))
            neighbors.add(new Point(x,y+1));

        return neighbors;
    }

    public List<Point> getDiagNeighbors(int x, int y)
    {
        List<Point> neighbors = new ArrayList<>();

        if(!outOfBounds(x-1,y-1))
            neighbors.add(new Point(x-1,y-1));
        if(!outOfBounds(x-1,y+1))
            neighbors.add(new Point(x-1,y+1));
        if(!outOfBounds(x+1,y-1))
            neighbors.add(new Point(x+1,y-1));
        if(!outOfBounds(x+1,y+1))
            neighbors.add(new Point(x+1,y+1));

        return neighbors;
    }
    public void removeStone(int x, int y)
    {
        board[x][y] = Stone.EMPTY;
    }

    private boolean outOfBounds(int x, int y)
    {
        return x < 0 || x > BOARD_SIZE-1 || y < 0 || y > BOARD_SIZE-1;
    }

    public static Stone[][] readTurn(String record)
    {
        Stone[][] result = new Stone[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < record.length(); i++)
        {
            int row = i / BOARD_SIZE;
            int col = i % BOARD_SIZE;
            switch (record.charAt(i))
            {
                case 'w':
                    result[row][col] = Stone.WHITE;
                    break;
                case 'b':
                    result[row][col] = Stone.BLACK;
                    break;
                case '.':
                    result[row][col] = Stone.EMPTY;
                    break;
                default:
                    System.err.println("Illegal Stone type on the board at " + row + ", " + col);
                    break;
            }
        }
        return result;
    }

    public static String recordTurn(Stone[][] board)
    {
        String result = "";
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                switch (board[i][j])
                {
                    case BLACK:
                        result += 'b';
                        break;
                    case WHITE:
                        result += 'w';
                        break;
                    case EMPTY:
                        result += '.';
                        break;
                    default:
                        System.err.println("Illegal Stone type on the board at " + i + ", " + j);
                        break;
                }
            }
        }
        return result;
    }

    public String getCurrentTurn()
    {
        return moveHistory.get(moveHistory.size()-1);
    }

    public String getTurn( int turn)
    {
        if(turn < moveHistory.size())
            return moveHistory.get(turn);

        return null;
    }

    public Stone[][] genStartingBoard()
    {
        Stone[][] newBoard = new Stone[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < newBoard.length; i++)
            for(int j = 0; j < newBoard[i].length; j++)
                newBoard[i][j] = Stone.EMPTY;
        return newBoard;
    }
    public void reset()
    {
        blackCaptured = 0;
        whiteCaptured = 0;
        String empty = moveHistory.get(0);
        moveHistory.clear();

        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                board[i][j] = Stone.EMPTY;

        moveHistory.add(empty);
    }


    public Stone[][] copyModel()
    {
        Stone[][] copy = new Stone[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                copy[i][j] = board[i][j];
        return copy;
    }

    public void updateModel(String update)
    {
        Stone[][] copy = readTurn(update);
        for(int i = 0; i < copy.length; i++)
            for(int j = 0; j < copy[i].length; j++)
                board[i][j] = copy[i][j];
    }

    public static void main(String [] args)
    {
        BoardModel bm = new BoardModel();
        bm.addStone(Stone.BLACK,0,0);
        bm.addStone(Stone.BLACK,0,1);
        bm.addStone(Stone.BLACK,0,2);
        bm.addStone(Stone.BLACK,0,3);
        bm.addStone(Stone.BLACK,0,4);
        bm.addStone(Stone.BLACK,0,5);
        bm.addStone(Stone.BLACK,0,6);
        bm.addStone(Stone.BLACK,0,7);
        bm.addStone(Stone.BLACK,0,8);
        bm.addStone(Stone.BLACK,8,0);
        bm.addStone(Stone.BLACK,8,1);
        bm.addStone(Stone.BLACK,8,2);
        bm.addStone(Stone.BLACK,8,3);
        bm.addStone(Stone.BLACK,8,4);
        bm.addStone(Stone.BLACK,8,5);
        bm.addStone(Stone.BLACK,8,6);
        bm.addStone(Stone.BLACK,8,7);
        bm.addStone(Stone.BLACK,8,8);
        bm.addStone(Stone.BLACK,0,0);
        bm.addStone(Stone.BLACK,1,0);
        bm.addStone(Stone.BLACK,2,0);
        bm.addStone(Stone.BLACK,3,0);
        bm.addStone(Stone.BLACK,4,0);
        bm.addStone(Stone.BLACK,5,0);
        bm.addStone(Stone.BLACK,6,0);
        bm.addStone(Stone.BLACK,7,0);
        bm.addStone(Stone.BLACK,8,0);
        bm.addStone(Stone.BLACK,0,8);
        bm.addStone(Stone.BLACK,1,8);
        bm.addStone(Stone.BLACK,2,8);
        bm.addStone(Stone.BLACK,3,8);
        bm.addStone(Stone.BLACK,4,8);
        bm.addStone(Stone.BLACK,5,8);
        bm.addStone(Stone.BLACK,6,8);
        bm.addStone(Stone.BLACK,7,8);
        bm.addStone(Stone.BLACK, 8, 8);

        System.out.println(bm.scoreBoard(Stone.BLACK));
    }

}

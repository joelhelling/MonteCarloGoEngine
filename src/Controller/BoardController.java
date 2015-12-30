package Controller;

import Model.*;
import View.BoardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GoEngine
 * Created by Joel on 4/5/2015.
 */
public class BoardController
{
    private BoardModel model;
    private BoardView view;
    private AI player1;
    private AI player2;
    private boolean paused;
    private boolean gamePlaying;

    private boolean player1pass;
    private boolean player2pass;

    private boolean playerTurn;

    public BoardController(BoardModel model, BoardView view)
    {
        this.model = model;
        this.view = view;
        this.paused = false;
        this.gamePlaying = false;
        this.player1pass = false;
        this.player2pass = false;
        this.playerTurn = false;

        this.player1 = new MonteAI(new BoardModel(), BoardModel.Stone.BLACK);
        this.player2 = new RandomAI(new BoardModel(),BoardModel.Stone.WHITE);

        view.addListenerPlay(new PlayListener());
        view.addListenerReset(new ResetListener());
        view.addListenerPause(new PauseListener());
        view.addListenerExit(new ExitListener());
    }

    public static void main(String[] args)
    {
        BoardModel model = new BoardModel();
        BoardView view = new BoardView(800,600);


        BoardController controller = new BoardController(model,view);
    }

    private class PlayListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            gamePlaying = true;
            int player1Points;
            int player2Points;
            // both players must pass for game to end
            while(!player1pass && !player2pass)
            {
                player1pass = false;
                player2pass = false;

                player1.updateModel(model.getCurrentTurn());
                Point move = player1.genMove();
                if(move == null)
                {
                    player1pass = true;
                    model.pass();
                }
                else if(model.isLegal(BoardModel.Stone.BLACK,move.getX(),move.getY()))
                {

                    int captures = model.playMove(BoardModel.Stone.BLACK, move.getX(), move.getY());
                    model.setBlackCaptured(model.getBlackCaptured() + captures);
                }
                view.update(model.getCurrentTurn());

                player2.updateModel(model.getCurrentTurn());
                move = player2.genMove();
                if(move == null)
                {
                    player2pass = true;
                    model.pass();
                }
                else if(model.isLegal(BoardModel.Stone.WHITE,move.getX(),move.getY()))
                {
                    int captures = model.playMove(BoardModel.Stone.WHITE, move.getX(), move.getY());
                    model.setWhiteCaptured(model.getWhiteCaptured() + captures);
                }
                view.update(model.getCurrentTurn());
            }
            // game is done
            // remove dead stones
            model.setBlackCaptured(model.getBlackCaptured() + model.removeDeadStones(BoardModel.Stone.BLACK));
            model.setWhiteCaptured(model.getWhiteCaptured() + model.removeDeadStones(BoardModel.Stone.WHITE));
            player1Points = model.scoreBoard(BoardModel.Stone.BLACK);
            player2Points = model.scoreBoard(BoardModel.Stone.WHITE);

            view.update(BoardModel.recordTurn(model.getBoard()));
            System.out.println("Total Black: " + player1Points);
            System.out.println("Total White: " + player2Points);
            if(player1Points > player2Points)
                System.out.println("B+" + (player1Points-player2Points));
            else if(player2Points > player1Points)
                System.out.println("W+" + (player2Points-player1Points));
            else
                System.out.println("Draw");
            gamePlaying = false;
        }
    }

    private class ExitListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            view.setVisible(false);
            view.dispose();
        }
    }

    private class PauseListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            JButton b = (JButton) e.getSource();
            if(b.getText().equals("Pause"))
            {
                paused = true;
                b.setText("Unpause");
            }
            else
            {
                paused = false;
                b.setText("Pause");
            }
        }
    }

    private class ResetListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(!gamePlaying) {
                model.reset();
                player1pass = false;
                player2pass = false;
                player1.resetBoard();
                player2.resetBoard();
                view.update(model.getCurrentTurn());
            }
        }
    }
}

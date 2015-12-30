package View;


import Model.BoardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GoEngine
 * Created by Joel on 4/5/2015.
 */
public class BoardView extends JFrame {
    private JButton play,reset,exit,pause;
    private BoardPanel bp;

    public BoardView(int width, int height)
    {
        Container contents = getContentPane();
        contents.setLayout(new BorderLayout());
        bp = new BoardPanel(BoardModel.BOARD_SIZE);
        contents.add(bp,BorderLayout.CENTER);
        GridLayout gl = new GridLayout(4,1);
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        play = new JButton();
        reset = new JButton();
        exit = new JButton();
        pause = new JButton();
        play.setText("Play");
        reset.setText("Reset");
        exit.setText("Exit");
        pause.setText("Pause");
        buttons.add(play);
        buttons.add(reset);
        //buttons.add(pause);
        buttons.add(exit);
        contents.add(buttons,BorderLayout.SOUTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (screenSize.getWidth() - width) / 2, (int) (screenSize.getHeight() - height) / 2);
        this.setSize(width, height);
        this.setTitle("AI Go Board");
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public void update(String move)
    {
        bp.update(move);
    }

    public void addListenerPlay(ActionListener listenerPlay)
    {
        this.play.addActionListener(listenerPlay);
    }

    public void addListenerReset(ActionListener listenerReset)
    {
        this.reset.addActionListener(listenerReset);
    }

    public void addListenerExit(ActionListener listenerExit)
    {
        this.exit.addActionListener(listenerExit);
    }

    public void addListenerPause(ActionListener listenerPause)
    {
        this.pause.addActionListener(listenerPause);
    }
    public class BoardPanel extends JPanel
    {
        private BoardModel.Stone[][] boardModel;
        private Color boardColor;
        private int boardSize;
        private int offset;
        private int margin;

        public BoardPanel(int size)
        {
            this.boardSize = size;
            boardModel = new BoardModel.Stone[size][size];
            boardColor = new Color(0x917D20);
            for(int i = 0; i < boardModel.length; i++)
            {
                for(int j = 0; j < boardModel[i].length; j++)
                {
                    boardModel[i][j] = BoardModel.Stone.EMPTY;
                }
            }
        }

        public void update(String move)
        {
            for(int i = 0; i < move.length(); i++)
            {
                int row = i / boardSize;
                int col = i % boardSize;
                switch (move.charAt(i))
                {
                    case 'w':
                        boardModel[row][col] = BoardModel.Stone.WHITE;
                        break;
                    case 'b':
                        boardModel[row][col] = BoardModel.Stone.BLACK;
                        break;
                    case '.':
                        boardModel[row][col] = BoardModel.Stone.EMPTY;
                        break;
                    default:
                        System.err.println("Illegal Stone type on the board at " + row + ", " + col);
                        break;
                }
            }
            super.repaint();
        }
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            int w = super.getWidth();
            int h = super.getHeight();
            this.offset = w/boardSize/2;
            this.margin = w/boardSize/2;
            g.setColor(boardColor);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.BLACK);
            for(int i = 0; i < this.boardSize; i++)
            {
                g.drawLine(margin+(offset*i),margin,margin+(offset*i),margin+(offset*(this.boardSize-1)));
                g.drawLine(margin,margin+(offset*i),margin+(offset*(this.boardSize-1)),margin+(offset*i));
            }
            for(int i = 0; i < this.boardModel.length; i++)
            {
                for(int j = 0; j < this.boardModel[i].length; j++)
                {
                    switch(this.boardModel[i][j])
                    {
                        case WHITE:
                            g.setColor(Color.WHITE);
                            g.fillOval(margin+(offset*i)-offset/2,margin+(offset*j)-offset/2,offset-5,offset-5);
                            break;
                        case BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(margin+(offset*i)-offset/2,margin+(offset*j)-offset/2,offset-5,offset-5);

                            break;
                        default:
                            //skip
                            break;
                    }
                }
            }
        }
    }
}

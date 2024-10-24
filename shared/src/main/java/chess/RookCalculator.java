package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        return getRookChessMoves(board, position, position, color, temp);
    }

    public ArrayList<ChessMove> getRookChessMoves(ChessBoard board, ChessPosition pos1, ChessPosition pos2,
                                                  ChessGame.TeamColor color, ArrayList<ChessMove> temp) {
        QueenCalculator queenCalc = new QueenCalculator();
        while (queenCalc.moveCheck(pos2, board, -1, 0, color)) {
            pos2 = new ChessPosition(pos2.getRow() - 1, pos2.getColumn());
            temp.add(new ChessMove(pos1, pos2, null));
        }
        pos2 = pos1;
        while (queenCalc.moveCheck(pos2, board, 1, 0, color)) {
            pos2 = new ChessPosition(pos2.getRow() + 1, pos2.getColumn());
            temp.add(new ChessMove(pos1, pos2, null));
        }
        pos2 = pos1;
        while (queenCalc.moveCheck(pos2, board, 0, -1, color)) {
            pos2 = new ChessPosition(pos2.getRow(), pos2.getColumn() - 1);
            temp.add(new ChessMove(pos1, pos2, null));
        }
        pos2 = pos1;
        while (queenCalc.moveCheck(pos2, board, 0, 1, color)) {
            pos2 = new ChessPosition(pos2.getRow(), pos2.getColumn() + 1);
            temp.add(new ChessMove(pos1, pos2, null));
        }

        return temp;
    }


}

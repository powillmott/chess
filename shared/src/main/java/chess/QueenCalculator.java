package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();

        new BishopCalculator().getBishopChessMoves(board,position,position,color,temp);
        new RookCalculator().getRookChessMoves(board,position,position,color,temp);

        return temp;
    }

    public boolean moveCheck(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (!position.inBounds(rowInc,colInc)) {
            return false;
        }
        if (board.getPiece(new ChessPosition(position.getRow(), position.getColumn())) != null) {
            if (board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor() != color) {
                return false;
            }
        }
        if (board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)) == null) {
            return true;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)).getTeamColor() != color;

    }


}

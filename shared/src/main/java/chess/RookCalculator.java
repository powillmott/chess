package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        ChessPosition pos = position;
        while (moveCheck(pos, board, -1, 0, color)) {
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn());
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, 1, 0, color)) {
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn());
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, 0, -1, color)) {
            pos = new ChessPosition(pos.getRow(), pos.getColumn() - 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, 0, 1, color)) {
            pos = new ChessPosition(pos.getRow(), pos.getColumn() + 1);
            temp.add(new ChessMove(position, pos, null));
        }

        return temp;
    }

    private boolean moveCheck(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
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

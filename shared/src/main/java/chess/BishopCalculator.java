package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        ChessPosition pos = position;
        while (moveCheck(pos, board, -1, -1, color)) {
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, 1, -1, color)) {
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, -1, 1, color)) {
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (moveCheck(pos, board, 1, 1, color)) {
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
            temp.add(new ChessMove(position, pos, null));
        }

        return temp;
    }

    private boolean moveCheck(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (position.getRow() + rowInc > 8 || position.getRow() + rowInc < 1 || position.getColumn() + colInc > 8 || position.getColumn() + colInc < 1) {
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

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        if(moveCheck(position, board, 2, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 2,position.getColumn() + 1), null));
        }
        if(moveCheck(position, board, 2, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 2,position.getColumn() - 1), null));
        }
        if(moveCheck(position, board, -2, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 2,position.getColumn() + 1), null));
        }
        if(moveCheck(position, board, -2, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 2,position.getColumn() - 1), null));
        }
        if(moveCheck(position, board, 1, 2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() + 2), null));
        }
        if(moveCheck(position, board, 1, -2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() - 2), null));
        }
        if(moveCheck(position, board, -1, 2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() + 2), null));
        }
        if(moveCheck(position, board, -1, -2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() - 2), null));
        }

        return temp;
    }

    private boolean moveCheck(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (!position.inBounds(rowInc,colInc)) {
            return false;
        }
        if (board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)) == null) {
            return true;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)).getTeamColor() != color;

    }


}

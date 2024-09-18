package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        if(moveCheck(position, board, 1, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() + 1), null));
        }
        if(moveCheck(position, board, 1, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() - 1), null));
        }
        if(moveCheck(position, board, -1, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() + 1), null));
        }
        if(moveCheck(position, board, -1, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() - 1), null));
        }
        if(moveCheck(position, board, 1, 0, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn()), null));
        }
        if(moveCheck(position, board, -1, 0, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn()), null));
        }
        if(moveCheck(position, board, 0, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow(),position.getColumn() + 1), null));
        }
        if(moveCheck(position, board, 0, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow(),position.getColumn() - 1), null));
        }

        return temp;
    }

    private boolean moveCheck(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (position.getRow() + rowInc > 8 || position.getRow() + rowInc < 1 || position.getColumn() + colInc > 8 || position.getColumn() + colInc < 1) {
            return false;
        }
        if (board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)) == null) {
            return true;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)).getTeamColor() != color;

    }


}

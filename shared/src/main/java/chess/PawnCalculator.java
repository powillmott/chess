package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {


        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        if(color == ChessGame.TeamColor.WHITE) {
            if (moveCheckTake(position, board, 1, 1, color)) {
                int rowInc = 1;
                int colInc = 1;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckTake(position, board, 1, -1, color)) {
                int rowInc = 1;
                int colInc = -1;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckSingle(position, board, 1, 0, color)) {
                int rowInc = 1;
                int colInc = 0;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckDouble(position, board, 2, 0, color)) {
                int rowInc = 2;
                int colInc = 0;
                addMove(position, rowInc, colInc, temp, color);
            }
        } else {
            if (moveCheckTake(position, board, -1, 1, color)) {
                int rowInc = -1;
                int colInc = 1;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckTake(position, board, -1, -1, color)) {
                int rowInc = -1;
                int colInc = -1;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckSingle(position, board, -1, 0, color)) {
                int rowInc = -1;
                int colInc = 0;
                addMove(position, rowInc, colInc, temp, color);
            }
            if (moveCheckDouble(position, board, -2, 0, color)) {
                int rowInc = -2;
                int colInc = 0;
                addMove(position, rowInc, colInc, temp, color);
            }
        }

        return temp;
    }

    private boolean moveCheckTake(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (position.getRow() + rowInc > 8 || position.getRow() + rowInc < 1 || position.getColumn() + colInc > 8 || position.getColumn() + colInc < 1) {
            return false;
        }
        if (board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)) == null) {
            return false;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)).getTeamColor() != color;
    }

    private boolean moveCheckSingle(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (position.getRow() + rowInc > 8 || position.getRow() + rowInc < 1 || position.getColumn() + colInc > 8 || position.getColumn() + colInc < 1) {
            return false;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc)) == null;
    }

    private boolean moveCheckDouble(ChessPosition position, ChessBoard board, int rowInc, int colInc, ChessGame.TeamColor color) {
        if (position.getRow() + rowInc > 8 || position.getRow() + rowInc < 1 || position.getColumn() + colInc > 8 || position.getColumn() + colInc < 1) {
            return false;
        }
        if(!((color == ChessGame.TeamColor.WHITE && position.getRow() == 2) || (color == ChessGame.TeamColor.BLACK && position.getRow() == 7))) {
            return false;
        }
        return board.getPiece(new ChessPosition(position.getRow() + rowInc, position.getColumn())) == null && board.getPiece(new ChessPosition(position.getRow() + (rowInc/2), position.getColumn())) == null;
    }

    private void addMove(ChessPosition position, int rowInc, int colInc, ArrayList<ChessMove> temp, ChessGame.TeamColor color) {
        if ((color == ChessGame.TeamColor.WHITE && position.getRow() + 1 == 8) || (color == ChessGame.TeamColor.BLACK && position.getRow() - 1 == 1)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc), ChessPiece.PieceType.QUEEN));
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc), ChessPiece.PieceType.ROOK));
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc), ChessPiece.PieceType.KNIGHT));
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc), ChessPiece.PieceType.BISHOP));
        } else {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + rowInc, position.getColumn() + colInc), null));
        }
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        return getBishopChessMoves(board, position, position, color, temp);
    }

    public ArrayList<ChessMove> getBishopChessMoves(ChessBoard board, ChessPosition position, ChessPosition pos, ChessGame.TeamColor color, ArrayList<ChessMove> temp) {
        QueenCalculator queenCalc = new QueenCalculator();
        while (queenCalc.moveCheck(pos, board, -1, -1, color)) {
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (queenCalc.moveCheck(pos, board, 1, -1, color)) {
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (queenCalc.moveCheck(pos, board, -1, 1, color)) {
            pos = new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
            temp.add(new ChessMove(position, pos, null));
        }
        pos = position;
        while (queenCalc.moveCheck(pos, board, 1, 1, color)) {
            pos = new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
            temp.add(new ChessMove(position, pos, null));
        }

        return temp;
    }


}

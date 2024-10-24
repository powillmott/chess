package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalculator implements MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        ArrayList<ChessMove> temp = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(new ChessPosition(position.getRow(), position.getColumn())).getTeamColor();
        KingCalculator kingCalc = new KingCalculator();
        if(kingCalc.moveCheck(position, board, 2, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 2,position.getColumn() + 1), null));
        }
        if(kingCalc.moveCheck(position, board, 2, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 2,position.getColumn() - 1), null));
        }
        if(kingCalc.moveCheck(position, board, -2, 1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 2,position.getColumn() + 1), null));
        }
        if(kingCalc.moveCheck(position, board, -2, -1, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 2,position.getColumn() - 1), null));
        }
        if(kingCalc.moveCheck(position, board, 1, 2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() + 2), null));
        }
        if(kingCalc.moveCheck(position, board, 1, -2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() + 1,position.getColumn() - 2), null));
        }
        if(kingCalc.moveCheck(position, board, -1, 2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() + 2), null));
        }
        if(kingCalc.moveCheck(position, board, -1, -2, color)) {
            temp.add(new ChessMove(position, new ChessPosition(position.getRow() - 1,position.getColumn() - 2), null));
        }

        return temp;
    }

}

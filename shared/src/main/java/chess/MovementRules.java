package chess;

import java.util.Collection;

public interface MovementRules {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}

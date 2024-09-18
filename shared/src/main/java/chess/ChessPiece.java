package chess;

import java.util.*;

import static chess.ChessPiece.PieceType.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */



public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */



    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> returnVal = new ArrayList<>();
        if(this.pieceType == BISHOP) {
            returnVal.addAll(new BishopCalculator().pieceMoves(board, myPosition));
        }
        if(this.pieceType == ROOK) {
            returnVal.addAll(new RookCalculator().pieceMoves(board, myPosition));
        }
        if(this.pieceType == KNIGHT) {
            returnVal.addAll(new KnightCalculator().pieceMoves(board, myPosition));
        }
        if(this.pieceType == KING) {
            returnVal.addAll(new KingCalculator().pieceMoves(board, myPosition));
        }
        if(this.pieceType == QUEEN) {
            returnVal.addAll(new QueenCalculator().pieceMoves(board, myPosition));
        }
        if(this.pieceType == PAWN) {
            returnVal.addAll(new PawnCalculator().pieceMoves(board, myPosition));
        }
        return returnVal;
    }
}

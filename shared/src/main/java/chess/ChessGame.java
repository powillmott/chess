package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final TeamColor team;
    private ChessBoard squares;
    private ChessPosition wKing = new ChessPosition(1, 5);
    private ChessPosition bKing = new ChessPosition(8, 5);
    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.squares = new ChessBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if(team == TeamColor.BLACK) {
            team = TeamColor.WHITE;
        }
        if(team == TeamColor.WHITE) {
            team = TeamColor.BLACK;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = this.squares.getPiece(startPosition).pieceMoves(this.squares,startPosition);
        Collection<ChessMove> returnval = new ArrayList<>();
        for(ChessMove move : moves){
            ChessPiece temp1 = this.squares.getPiece(startPosition);
            ChessPiece temp2 = this.squares.getPiece(move.getEndPosition());
            this.squares.addPiece(startPosition, null);
            this.squares.addPiece(move.getEndPosition(), temp1);
            if(!isInCheck(temp1.getTeamColor())){
                returnval.add(move);
            }
            this.squares.addPiece(startPosition, temp1);
            this.squares.addPiece(move.getEndPosition(), temp2);
        }
        return returnval;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                if(this.squares.getPiece(new ChessPosition(i, j)) != null) {
                    Collection<ChessMove> temp = this.squares.getPiece(new ChessPosition(i, j)).pieceMoves(this.squares, new ChessPosition(i, j));
                    for (ChessMove move : temp) {
                        if (Objects.equals(move.getEndPosition(),wKing) && teamColor == TeamColor.WHITE) {
                            return true;
                        }
                        if(Objects.equals(move.getEndPosition(),bKing) && teamColor == TeamColor.BLACK) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.squares = board;
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if(this.squares.getPiece(new ChessPosition(i,j)) != null) {
                    if (this.squares.getPiece(new ChessPosition(i, j)).getTeamColor() == TeamColor.WHITE && this.squares.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                        wKing = new ChessPosition(i, j);
                    }
                    if (this.squares.getPiece(new ChessPosition(i, j)).getTeamColor() == TeamColor.BLACK && this.squares.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                        bKing = new ChessPosition(i, j);
                    }
                }
            }
        }

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}

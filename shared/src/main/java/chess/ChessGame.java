package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private TeamColor team;
    private ChessBoard squares;
    private ChessPosition wKing = new ChessPosition(1, 5);
    private ChessPosition bKing = new ChessPosition(8, 5);
    public ChessGame() {
        team = TeamColor.WHITE;
        squares = new ChessBoard();
        squares.resetBoard();;

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
        this.team = team;
    }

    public void changeTeamColor() {
        if(TeamColor.WHITE == team) {
            team = TeamColor.BLACK;
        }
        else{
            team = TeamColor.WHITE;
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
            if(temp1.getPieceType() == ChessPiece.PieceType.KING) {
                if (temp1.getTeamColor() == TeamColor.BLACK) {
                    bKing = move.getEndPosition();
                }
                if (temp1.getTeamColor() == TeamColor.WHITE) {
                    wKing = move.getEndPosition();
                }
            }
            if(!isInCheck(team)){
                returnval.add(move);
            }
            if(temp1.getPieceType() == ChessPiece.PieceType.KING){
                if(temp1.getTeamColor() == TeamColor.WHITE){
                    wKing = startPosition;
                }
                else{
                    bKing = startPosition;
                }
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
        //check if move is valid (through exception if not)
        boolean pass = false;
        if(squares.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("no piece found");
        }
        if(squares.getPiece(move.getStartPosition()).getTeamColor() != team) {
            throw new InvalidMoveException("wrong team");
        }
        Collection<ChessMove> moves = this.validMoves(move.getStartPosition());
        if(!moves.contains(move)){
            throw new InvalidMoveException("invalid move");
        }
        //make move
        ChessPiece temp = this.squares.getPiece(move.getStartPosition());
        this.squares.addPiece(move.getStartPosition(), null);
        if(move.getPromotionPiece() != null){
            this.squares.addPiece(move.getEndPosition(), new ChessPiece(team,move.getPromotionPiece()));
        } else{
            this.squares.addPiece(move.getEndPosition(), temp);
        }

        //update king location if necessary
        if(temp.getPieceType() == ChessPiece.PieceType.KING){
            if(temp.getTeamColor() == TeamColor.WHITE){
                wKing = move.getEndPosition();
            }
            if(temp.getTeamColor() == TeamColor.BLACK){
                bKing = move.getEndPosition();
            }
        }
        //change team turn
        changeTeamColor();
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
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessMove> temp = new ArrayList<>();
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if(this.squares.getPiece(new ChessPosition(i, j)) != null) {
                    if(this.squares.getPiece(new ChessPosition(i,j)).getTeamColor() == teamColor) {
                        temp.addAll(validMoves(new ChessPosition(i,j)));
                    }
                }
            }
        }
        return temp.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        Collection<ChessMove> temp = new ArrayList<>();
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if(this.squares.getPiece(new ChessPosition(i, j)) != null) {
                    if(this.squares.getPiece(new ChessPosition(i,j)).getTeamColor() == teamColor) {
                        temp.addAll(validMoves(new ChessPosition(i,j)));
                    }
                }
            }
        }
        return temp.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.squares = board;
        wKing = null;
        bKing = null;
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
        return this.squares;
    }
}

package chess.repository;

import chess.dao.GameDao;
import chess.dao.PieceDao;
import chess.dao.RoomDao;
import chess.dao.TeamDao;
import chess.domain.ChessGame;
import chess.domain.Position;
import chess.domain.Room;
import chess.domain.piece.Piece;
import chess.domain.team.BlackTeam;
import chess.domain.team.Team;
import chess.domain.team.WhiteTeam;
import dto.MoveDto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ChessRepository {
    private final RoomDao roomDao;
    private final GameDao gameDao;
    private final TeamDao teamDao;
    private final PieceDao pieceDao;

    public ChessRepository(RoomDao roomDao, GameDao gameDao, TeamDao teamDao, PieceDao pieceDao) {
        this.roomDao = roomDao;
        this.gameDao = gameDao;
        this.teamDao = teamDao;
        this.pieceDao = pieceDao;
    }

    public void createRoom(final ChessGame chessGame, final Room room) {
        long gameId = gameDao.create(chessGame);

        WhiteTeam whiteTeam = chessGame.getWhiteTeam();
        teamDao.create(whiteTeam, gameId);
        pieceDao.create(whiteTeam.getPiecePosition(), whiteTeam.getName(), gameId);

        BlackTeam blackTeam = chessGame.getBlackTeam();
        teamDao.create(blackTeam, gameId);
        pieceDao.create(blackTeam.getPiecePosition(), blackTeam.getName(), gameId);

        room.setGameId(gameId);
        roomDao.create(room);
    }

    public Room loadRoom(long roomId) {
        return roomDao.load(roomId);
    }

    public List<Room> loadAllRoom() {
        return roomDao.loadAll();
    }

    public ChessGame loadGame(final long roomId) {
        Room room = roomDao.load(roomId);
        long gameId = room.getGameId();
        boolean isEnd = gameDao.isEnd(gameId);

        Team team = teamDao.load(gameId, WhiteTeam.DEFAULT_NAME);
        final Map<Position, Piece> whitePieces = pieceDao.load(gameId, WhiteTeam.DEFAULT_NAME);
        WhiteTeam whiteTeam = new WhiteTeam(team.getName(), team.isCurrentTurn(), whitePieces);

        team = teamDao.load(gameId, BlackTeam.DEFAULT_NAME);
        final Map<Position, Piece> blackPieces = pieceDao.load(gameId, BlackTeam.DEFAULT_NAME);
        BlackTeam blackTeam = new BlackTeam(team.getName(), team.isCurrentTurn(), blackPieces);

        whiteTeam.setEnemy(blackTeam);
        blackTeam.setEnemy(whiteTeam);

        return new ChessGame(whiteTeam, blackTeam, isEnd);
    }

    public void saveGame(final long roomId, final ChessGame chessGame, MoveDto moveDto) {
        Room room = roomDao.load(roomId);
        long gameId = room.getGameId();

        if (chessGame.isEnd()) {
            gameDao.update(gameId, true);
        }

        WhiteTeam whiteTeam = chessGame.getWhiteTeam();
        BlackTeam blackTeam = chessGame.getBlackTeam();

        teamDao.update(gameId, whiteTeam);
        teamDao.update(gameId, blackTeam);

        pieceDao.delete(gameId, moveDto);
        pieceDao.update(gameId, moveDto);
    }
}

package wooteco.chess.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.chess.domain.board.Board;
import wooteco.chess.domain.board.BoardEntity;
import wooteco.chess.domain.board.BoardRepository;
import wooteco.chess.domain.piece.Piece;
import wooteco.chess.domain.piece.PieceType;
import wooteco.chess.domain.piece.Team;
import wooteco.chess.domain.position.Position;
import wooteco.chess.domain.result.GameResult;
import wooteco.chess.domain.result.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    @Autowired
    private RoomService roomService;

    public BoardService(final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public Status movePiece(final Long roomId, final String fromPosition, final String toPosition) {
        List<BoardEntity> boardEntities = boardRepository.findByRoomId(roomId);
        Board board = Board.createLoadedBoard(boardEntities);
        Piece piece = board.findBy(Position.of(fromPosition));

        if (piece.isNotSameTeam(Team.of(roomService.findTurnById(roomId)))) {
            throw new IllegalArgumentException("체스 게임 순서를 지켜주세요.");
        }
        Status status = board.isMovable(fromPosition, toPosition);
        if (status.isMove()) {
            boardRepository.updateBoardPosition(roomId, fromPosition, PieceType.BLANK.name());
            boardRepository.updateBoardPosition(roomId, toPosition, piece.getNextPiece().getName());
        }

        roomService.updateTurn(roomId);
        if (board.isFinished()) {
            status = Status.Finish;
        }
        return status;
    }

    public Map<String, String> showScoreStatus(final Long roomId) {
        GameResult gameResult = new GameResult();
        List<BoardEntity> boardEntities = boardRepository.findByRoomId(roomId);
        Board board = Board.createLoadedBoard(boardEntities);

        double blackScore = gameResult.calculateScore(board, Team.BLACK);
        double whiteScore = gameResult.calculateScore(board, Team.WHITE);

        Map<String, String> model = new HashMap<>();
        model.put("black", String.valueOf(blackScore));
        model.put("white", String.valueOf(whiteScore));
        return model;
    }

    public String receiveWinner(final Long roomId) {
        roomService.updateTurn(roomId);
        return roomService.findTurnById(roomId);
    }

    public boolean isFinish(final Board board) {
        return board.isFinished();
    }

}
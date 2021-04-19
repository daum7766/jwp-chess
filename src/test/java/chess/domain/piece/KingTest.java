package chess.domain.piece;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import chess.domain.Position;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class KingTest {
    @Test
    @DisplayName("King이 정상으로 생성되는지 테스트한다.")
    void init() {
        assertThatCode(King::new)
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource(value = {"c3", "c4", "c5", "d3", "d5", "e3", "e4", "e5"})
    @DisplayName("King이 규칙상 움직일 수 있는 곳이라면, 참을 반환한다.")
    void when_king_can_move_according_to_rule_return_true(final String dest) {
        final King king = new King();
        final Position current = Position.of("d4");
        final Position destination = Position.of(dest);
        final Map<Position, Piece> chessBoard = new HashMap<>();
        chessBoard.put(current, king);

        assertThat(king.isMovable(current, destination, chessBoard)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"c3", "c4", "c5", "d3", "d5", "e3", "e4", "e5"})
    @DisplayName("King이 규칙상 움직일 수 업는 곳이라면, 거짓을 반환한다.")
    void when_king_cannot_move_according_to_rule_return_false(final String dest) {
        final King king = new King();
        final Position current = Position.of("g6");
        final Position destination = Position.of(dest);
        final Map<Position, Piece> chessBoard = new HashMap<>();
        chessBoard.put(current, king);

        assertThat(king.isMovable(current, destination, chessBoard)).isFalse();
    }
}

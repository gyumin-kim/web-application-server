package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UuidTest {

	private final String DELIMITER = "-";
	private final int UUID_FIELD_SIZE = 5;
	private final int TIME_LOW_LENGTH = 8;
	private final int TIME_MID_LENGTH = 4;
	private final int TIME_HI_AND_VERSION_LENGTH = 4;
	private final int CLOCK_SEQ_HI_AND_RES_LENGTH = 4;
	private final int NODE_LENGTH = 12;

	@DisplayName("UUID 생성 및 필드별 length 테스트")
	@Test
	void uuid() {
		// when
		UUID uuid = UUID.randomUUID();

		// then
		String[] fields = uuid.toString().split(DELIMITER);
		assertThat(fields).hasSize(UUID_FIELD_SIZE);
		assertThat(fields[0].length()).isEqualTo(TIME_LOW_LENGTH);
		assertThat(fields[1].length()).isEqualTo(TIME_MID_LENGTH);
		assertThat(fields[2].length()).isEqualTo(TIME_HI_AND_VERSION_LENGTH);
		assertThat(fields[3].length()).isEqualTo(CLOCK_SEQ_HI_AND_RES_LENGTH);
		assertThat(fields[4].length()).isEqualTo(NODE_LENGTH);
	}
}

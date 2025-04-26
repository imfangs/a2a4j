package org.a2a4j.models.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import lombok.experimental.SuperBuilder;


/**
 * Generic error response for the A2A protocol.
 *
 * @param <R> the type of the result
 */
@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<R> extends JsonRpcResponse<R> {

    // Constructor removed - Handled by Lombok's @SuperBuilder and @NoArgsConstructor

}

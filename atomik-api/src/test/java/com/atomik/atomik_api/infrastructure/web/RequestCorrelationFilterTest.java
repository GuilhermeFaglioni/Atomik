package com.atomik.atomik_api.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestCorrelationFilterTest {

    private final RequestCorrelationFilter filter = new RequestCorrelationFilter();

    @Test
    @DisplayName("Should propagate request id header and clear MDC after request")
    void shouldPropagateRequestIdHeaderAndClearMdcAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestCorrelationFilter.REQUEST_ID_HEADER, "req-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("req-123", response.getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER));
        assertNull(MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY));
    }

    @Test
    @DisplayName("Should generate request id when header missing")
    void shouldGenerateRequestIdWhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String generatedRequestId = response.getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER);
        assertNotNull(generatedRequestId);
        assertFalse(generatedRequestId.isBlank());
        assertNull(MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY));
    }
}

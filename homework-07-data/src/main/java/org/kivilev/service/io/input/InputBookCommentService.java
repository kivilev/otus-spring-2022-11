package org.kivilev.service.io.input;

import org.apache.commons.lang3.tuple.Pair;

public interface InputBookCommentService {
    Pair<Long, String> getNewComment();

    long getRemoveId();
}

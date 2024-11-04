// dao/ContentBlockDAO.java
package com.etextbook.dao;

import com.etextbook.model.ContentBlock;
import java.util.List;
import java.util.Optional;

public interface ContentBlockDAO extends BaseDAO<ContentBlock, Integer> {
    List<ContentBlock> findBySection(String sectionId);
    List<ContentBlock> findByType(String contentType);
    List<ContentBlock> findBySequenceOrder(String sectionId);
    Optional<ContentBlock> findBySequenceNumber(String sectionId, Integer sequenceNumber);
}

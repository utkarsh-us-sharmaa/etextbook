package com.etextbook.dao;

import com.etextbook.model.Section;
import java.util.List;
import java.util.Optional;

public interface SectionDAO extends BaseDAO<Section, String> {
    List<Section> findByChapter(String chapterId);
    Optional<Section> findBySectionNumber(String sectionNumber, String chapterId);
    List<Section> findSectionsInOrder(String chapterId);
}
   

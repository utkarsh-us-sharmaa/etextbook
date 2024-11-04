package com.etextbook.dao;

import com.etextbook.model.Chapter;
import java.util.List;
import java.util.Optional;

public interface ChapterDAO extends BaseDAO<Chapter, String> {
    List<Chapter> findByTextbook(Integer textbookId);
    Optional<Chapter> findByChapterNumber(String chapterNumber, Integer textbookId);
    List<Chapter> findChaptersInOrder(Integer textbookId);
    Optional<Chapter> findById(String chapterId);
}

package kr.ac.jbnu.ksh.blogtp.category.service;

import kr.ac.jbnu.ksh.blogtp.category.domain.Category;
import kr.ac.jbnu.ksh.blogtp.category.dto.CategoryRequest;
import kr.ac.jbnu.ksh.blogtp.category.dto.CategoryResponse;
import kr.ac.jbnu.ksh.blogtp.category.repository.CategoryRepository;
import kr.ac.jbnu.ksh.blogtp.common.error.ApiException;
import kr.ac.jbnu.ksh.blogtp.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryResponse::from);
    }

    @Transactional(readOnly = true)
    public Category getEntityOrNull(Long id) {
        if (id == null) return null;
        return categoryRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        categoryRepository.findByName(req.name()).ifPresent(c -> {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE, "카테고리명이 중복입니다.");
        });
        Category c = new Category(req.name());
        return CategoryResponse.from(categoryRepository.save(c));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.findByName(req.name()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException(ErrorCode.DUPLICATE_RESOURCE, "카테고리명이 중복입니다.");
            }
        });
        c.rename(req.name());
        return CategoryResponse.from(c);
    }

    @Transactional
    public void delete(Long id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(c);
    }
}

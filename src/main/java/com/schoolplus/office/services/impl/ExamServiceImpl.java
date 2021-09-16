package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.ExamService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.*;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamSkeletonRepository examSkeletonRepository;
    private final ExamFieldRepository examFieldRepository;
    private final OrganizationRepository organizationRepository;
    private final ExamTypeRepository examTypeRepository;
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final GradeRepository gradeRepository;
    private final ExamResultItemRepository examResultItemRepository;
    private final ExamMapper examMapper;
    private final ExamSkeletonMapper examSkeletonMapper;
    private final ExamTypeMapper examTypeMapper;
    private final ExamResultMapper examResultMapper;
    private final ExamResultItemMapper examResultItemMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamDto> getExams(Pageable pageable) {
        Page<Exam> pages = examRepository.findAll(pageable);

        return examMapper.examToExamDto(pages.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamDto> getExamsByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Exam> exams = examRepository.findAllByOrganization(organization, pageable);

        return examMapper.examToExamDto(exams.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamTypeDto> getExamTypes(Pageable pageable) {
        Page<ExamType> examTypes = examTypeRepository.findAll(pageable);

        return examTypeMapper.examTypeToExamTypeDto(examTypes.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamTypeDto> getExamTypesByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<ExamType> examTypes = examTypeRepository.findAllByOrganization(organization, pageable);

        return examTypeMapper.examTypeToExamTypeDto(examTypes.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamSkeletonDto> getExamSkeletons(Pageable pageable) {
        Page<ExamSkeleton> examSkeletons = examSkeletonRepository.findAll(pageable);

        return examSkeletonMapper.examSkeletonDtoToExamSkeleton(examSkeletons.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamSkeletonDto> getExamSkeletonsByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<ExamSkeleton> examSkeletons = examSkeletonRepository.findAllByOrganization(organization, pageable);

        return examSkeletonMapper.examSkeletonDtoToExamSkeleton(examSkeletons.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamResultDto> getExamResults(Pageable pageable) {
        Page<ExamResult> examResults = examResultRepository.findAll(pageable);

        return examResultMapper.examResultToExamResultDtoWithoutItemsList(examResults.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamResultDto> getExamResultsByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<ExamResult> examResults = examResultRepository.findAllByExamOrganization(organization, pageable);

        return examResultMapper.examResultToExamResultDtoWithoutItemsList(examResults.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamResultDto> getExamResultsByStudent(UUID studentId, Pageable pageable) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId);
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        Page<ExamResult> examResults = examResultRepository.findAllByExamResultItems_Student(student, pageable);

        return examResultMapper.examResultToExamResultDtoWithoutItemsList(examResults.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamResultDto> getExamResultsByClassroom(Long classRoomId, Pageable pageable) {
        Classroom classroom = classroomRepository.findById(classRoomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId: {}]", classRoomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        Page<ExamResult> examResults = examResultRepository.findAllByExamResultItems_Classroom(classroom, pageable);

        return examResultMapper.examResultToExamResultDtoWithoutItemsList(examResults.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exams'))")
    @Override
    public List<ExamResultDto> getExamResultsByGrade(Long gradeId, Pageable pageable) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                   log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                   throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        Page<ExamResult> examResults = examResultRepository.findAllByExamResultItems_Classroom_Grade(grade, pageable);

        return examResultMapper.examResultToExamResultDtoWithoutItemsList(examResults.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exam'))")
    @Override
    public ExamDto getExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.warn("Exam with given id does not exists [examId: {}]", examId);
                    throw new ExamNotFoundException(ErrorDesc.EXAM_NOT_FOUND.getDesc());
                });

        return examMapper.examToExamDto(exam);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exam'))")
    @Override
    public ExamTypeDto getExamType(Long examTypeId) {
        ExamType examType = examTypeRepository.findById(examTypeId)
                .orElseThrow(() -> {
                    log.warn("Exam Type with given id does not exists [examTypeId: {}]", examTypeId);
                    throw new ExamTypeNotFoundException(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc());
                });

        return examTypeMapper.examTypeToExamTypeDto(examType);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exam'))")
    @Override
    public ExamSkeletonDto getExamSkeleton(Long examSkeletonId) {
        ExamSkeleton examSkeleton = examSkeletonRepository.findById(examSkeletonId)
                .orElseThrow(() -> {
                    log.warn("Exam Skeleton with given id does not exists [examSkeletonId: {}]", examSkeletonId);
                    throw new ExamSkeletonNotFoundException(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc());
                });

        return examSkeletonMapper.examSkeletonDtoToExamSkeleton(examSkeleton);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('read:exam'))")
    @Override
    public ExamResultDto getExamResult(Long examResultId) {
        ExamResult examResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> {
                    log.warn("Exam Result with given id does not exists [examResultId: {}]", examResultId);
                    throw new ExamResultNotFoundException(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc());
                });

        return examResultMapper.examResultToExamResultDto(examResult);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('create:exam'))")
    @Override
    public ExamDto createExam(CreatingExamDto creatingExam) {
        Exam exam = new Exam();
        exam.setExamName(creatingExam.getExamName());

        Organization organization = organizationRepository.findById(creatingExam.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingExam.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        exam.setOrganization(organization);

        ExamType examType = examTypeRepository.findById(creatingExam.getExamTypeId())
                .orElseThrow(() -> {
                    log.warn("Exam Type with given id does not exists [examTypeId: {}]", creatingExam.getExamTypeId());
                    throw new ExamTypeNotFoundException(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc());
                });

        exam.setExamType(examType);

        ExamSkeleton examSkeleton = examSkeletonRepository.findById(creatingExam.getExamSkeletonId())
                .orElseThrow(() -> {
                    log.warn("Exam Skeleton with given id does not exists [examSkeletonId: {}]", creatingExam.getExamSkeletonId());
                    throw new ExamSkeletonNotFoundException(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc());
                });

        exam.setExamSkeleton(examSkeleton);

        examRepository.save(exam);

        log.info("The Exam has been created successfully [examId: {}, performedBy: {}]",
                exam.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return examMapper.examToExamDto(exam);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('create:exam'))")
    @Override
    public ExamSkeletonDto createExamSkeleton(CreatingExamSkeletonDto creatingExamSkeleton) {
        ExamSkeleton examSkeleton = new ExamSkeleton();
        examSkeleton.setExamSkeletonName(creatingExamSkeleton.getExamSkeletonName());

        Organization organization = organizationRepository.findById(creatingExamSkeleton.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingExamSkeleton.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        examSkeleton.setOrganization(organization);

        creatingExamSkeleton.getFields().forEach(examFieldDto -> {
            ExamField examField = new ExamField();
            examField.setExamFieldName(examFieldDto.getExamFieldName());
            examField.setReferenceField(examFieldDto.getReferenceField());
            examField.setIsReference(examFieldDto.getIsReference());

            examSkeleton.addExamField(examField);
        });

        examSkeletonRepository.save(examSkeleton);

        log.info("The Exam Skeleton has been created successfully [skeletonId: {}, performedBy: {}]",
                examSkeleton.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return examSkeletonMapper.examSkeletonDtoToExamSkeleton(examSkeleton);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('create:exam'))")
    @Override
    public ExamTypeDto createExamType(CreatingExamTypeDto creatingExamType) {
        ExamType examType = new ExamType();
        examType.setExamTypeName(creatingExamType.getExamTypeName());
        examType.setExamDuration(creatingExamType.getExamDuration());
        examType.setNumberOfQuestion(creatingExamType.getNumberOfQuestion());

        Organization organization = organizationRepository.findById(creatingExamType.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingExamType.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        examType.setOrganization(organization);

        examTypeRepository.save(examType);

        log.info("The Exam Type has been created successfully [examTypeId: {}, performedBy: {}]",
                examType.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return examTypeMapper.examTypeToExamTypeDto(examType);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('create:exam'))")
    @Override
    public ExamResultDto createExamResult(Long examId, MultipartFile result) {
        try {
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> {
                        log.warn("Exam with given id does not exists [examId: {}]", examId);
                        throw new ExamNotFoundException(ErrorDesc.EXAM_NOT_FOUND.getDesc());
                    });

            Workbook workbook = new HSSFWorkbook(result.getInputStream());
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            Sheet worksheet = workbook.getSheetAt(0);

            Row firstRow = worksheet.getRow(0);

            Iterator<Cell> firstRowIterator = firstRow.iterator();

            Map<Integer, String> rowsName = new HashMap<>();

            int rowIndex = 0;
            while (firstRowIterator.hasNext()) {
                Cell cell = firstRowIterator.next();

                rowsName.put(rowIndex, cell.getStringCellValue());
                rowIndex++;
            }

            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            String studentNoCellName = exam.getExamSkeleton().getExamFields().stream()
                    .filter(examField -> examField.getReferenceField().equals(ReferenceField.STUDENT_NO))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.warn("Student cell position is not defined in system [examId: {}]", examId);
                        throw new CellNotDefinedException(ErrorDesc.STUDENT_CELL_NOT_DEFINED.getDesc());
                    })
                    .getExamFieldName();

            String classroomNoCellName = exam.getExamSkeleton().getExamFields().stream()
                    .filter(examField -> examField.getReferenceField().equals(ReferenceField.CLASSROOM_NO))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.warn("Classroom cell position is not defined in system [examId: {}]", examId);
                        throw new CellNotDefinedException(ErrorDesc.CLASSROOM_CELL_NOT_DEFINED.getDesc());
                    })
                    .getExamFieldName();

            String sortableNoCellName = exam.getExamSkeleton().getExamFields().stream()
                    .filter(examField -> examField.getReferenceField().equals(ReferenceField.SORTABLE))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.warn("Sortable cell position is not defined in system [examId: {}]", examId);
                        throw new CellNotDefinedException(ErrorDesc.SORTABLE_CELL_NOT_DEFINED.getDesc());
                    })
                    .getExamFieldName();

            for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
                if (i > 0) {
                    Row row = worksheet.getRow(i);

                    ExamResultItem examResultItem = new ExamResultItem();

                    for (int ic = 0; ic < row.getPhysicalNumberOfCells(); ic++) {
                        Cell cell = row.getCell(ic);

                        String rowVal;

                        if (cell.getCellType().equals(CellType.STRING)) {
                            rowVal = String.valueOf(cell.getStringCellValue());
                        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                            rowVal = String.valueOf(cell.getNumericCellValue());
                        } else if (cell.getCellType().equals(CellType.BLANK)) {
                            rowVal = null;
                        } else if (cell.getCellType().equals(CellType._NONE)) {
                            rowVal = null;
                        } else {
                            rowVal = null;
                        }

                        if (rowsName.get(ic).equals(studentNoCellName) && rowVal != null) {
                            Optional<Student> student = userRepository.findByStudentNumber((long) Math.round(Float.parseFloat(rowVal)));

                            student.ifPresent(examResultItem::setStudent);
                        }

                        if (rowsName.get(ic).equals(classroomNoCellName) && rowVal != null) {
                            Optional<Classroom> classroom = classroomRepository.findByClassNumber((long) Math.round(Float.parseFloat(rowVal)));

                            classroom.ifPresent(examResultItem::setClassroom);
                        }

                        if (rowsName.get(ic).equals(sortableNoCellName) && rowVal != null) {
                            examResultItem.setSortable(new BigDecimal(rowVal.replace(",", ".")));
                        }

                        examResultItem.addResultData(rowsName.get(ic), rowVal);
                    }

                    examResult.addExamResultItem(examResultItem);
                }
            }

            examResultRepository.save(examResult);

            log.info("The Result has been created successfully [resultId: {}, performedBy: {}}", examResult.getId(),
                    SecurityContextHolder.getContext().getAuthentication().getName());

            return examResultMapper.examResultToExamResultDto(examResult);
        } catch (IOException e) {
            log.warn("The excel file couldn't able to read");
            throw new FileNotReadableException(ErrorDesc.FILE_NOT_READABLE.getDesc());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('create:exam'))")
    @Override
    public ExamResultItemDto getExamResultItem(Long examResultItemId) {
        ExamResultItem examResultItem = examResultItemRepository.findById(examResultItemId)
                .orElseThrow(() -> {
                    log.warn("Exam Result Item with given id does not exists [examResultItemId: {}]", examResultItemId);
                    throw new ExamResultItemNotFoundException(ErrorDesc.EXAM_RESULT_ITEM_NOT_FOUND.getDesc());
                });

        return examResultItemMapper.examResultItemToExamResultItemDto(examResultItem);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void updateExam(Long examId, EditingExamDto editingExam) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.warn("Exam with given id does not exists [examId: {}]", examId);
                    throw new ExamNotFoundException(ErrorDesc.EXAM_NOT_FOUND.getDesc());
                });

        if (editingExam.getExamName() != null) {
            exam.setExamName(editingExam.getExamName());
        }

        if (editingExam.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingExam.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingExam.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            exam.setOrganization(organization);
        }

        if (editingExam.getExamTypeId() != null) {
            ExamType examType = examTypeRepository.findById(editingExam.getExamTypeId())
                    .orElseThrow(() -> {
                        log.warn("Exam Type with given id does not exists [examTypeId: {}]", editingExam.getExamTypeId());
                        throw new ExamTypeNotFoundException(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc());
                    });

            exam.setExamType(examType);
        }

        if (editingExam.getExamSkeletonId() != null) {
            ExamSkeleton examSkeleton = examSkeletonRepository.findById(editingExam.getExamSkeletonId())
                    .orElseThrow(() -> {
                        log.warn("Exam Skeleton with given id does not exists [examSkeletonId: {}]", editingExam.getExamSkeletonId());
                        throw new ExamSkeletonNotFoundException(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc());
                    });

            exam.setExamSkeleton(examSkeleton);
        }

        examRepository.save(exam);

        log.info("The Exam has been updated succesfully [examId: {}, performedBy: {}]",
                examId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void updateExamType(Long examTypeId, EditingExamTypeDto editingExamType) {
        ExamType examType = examTypeRepository.findById(examTypeId)
                .orElseThrow(() -> {
                    log.warn("Exam Type with given id does not exists [examTypeId: {}]", examTypeId);
                    throw new ExamTypeNotFoundException(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc());
                });

        if (editingExamType.getExamTypeName() != null) {
            examType.setExamTypeName(editingExamType.getExamTypeName());
        }

        if (editingExamType.getExamDuration() != null) {
            examType.setExamDuration(editingExamType.getExamDuration());
        }

        if (editingExamType.getNumberOfQuestion() != null) {
            examType.setNumberOfQuestion(editingExamType.getNumberOfQuestion());
        }

        if (editingExamType.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingExamType.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingExamType.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            examType.setOrganization(organization);
        }

        examTypeRepository.save(examType);

        log.info("The Exam Type has been updated successfully [examTypeId: {}, performedBy: {}]",
                examTypeId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void updateExamSkeleton(Long examSkeletonId, EditingExamSkeletonDto editingExamSkeleton) {
        ExamSkeleton examSkeleton = examSkeletonRepository.findById(examSkeletonId)
                .orElseThrow(() -> {
                    log.warn("Exam Skeleton with given id does not exists [examSkeletonId: {}]", editingExamSkeleton);
                    throw new ExamSkeletonNotFoundException(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc());
                });

        if (editingExamSkeleton.getExamSkeletonName() != null) {
            examSkeleton.setExamSkeletonName(editingExamSkeleton.getExamSkeletonName());
        }

        if (editingExamSkeleton.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingExamSkeleton.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingExamSkeleton.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            examSkeleton.setOrganization(organization);
        }

        if (editingExamSkeleton.getRemovedFields() != null
                && editingExamSkeleton.getRemovedFields().size() > 0) {
            editingExamSkeleton.getRemovedFields().forEach(removedFieldId -> {
                ExamField examField = examFieldRepository.findById(removedFieldId)
                        .orElseThrow(() -> {
                            log.warn("Exam Field with given id does not exists [examFieldId: {}]", removedFieldId);
                            throw new ExamFieldNotFoundException(ErrorDesc.EXAM_FIELD_NOT_FOUND.getDesc());
                        });

                examSkeleton.removeExamField(examField);
            });
        }

        if (editingExamSkeleton.getAddedFields() != null
                && editingExamSkeleton.getAddedFields().size() > 0) {
            editingExamSkeleton.getAddedFields().forEach(creatingExamField -> {
                ExamField examField = new ExamField();
                examField.setExamFieldName(creatingExamField.getExamFieldName());
                examField.setReferenceField(creatingExamField.getReferenceField());
                examField.setIsReference(creatingExamField.getIsReference());

                examSkeleton.addExamField(examField);
            });
        }

        examSkeletonRepository.save(examSkeleton);

        log.info("The Exam Skeleton has been updated successfully [examTypeId: {}, performedBy: {}]",
                examSkeletonId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void updateExamResult(Long examResultId, EditingExamResultDto editingExamResult) {
        ExamResult examResult = examResultRepository.findById(examResultId)
                .orElseThrow(() -> {
                    log.warn("Exam Result with given id does not exists [examResultId: {}]", examResultId);
                    throw new ExamResultNotFoundException(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc());
                });

        if (editingExamResult.getExamId() != null) {
            Exam exam = examRepository.findById(editingExamResult.getExamId())
                    .orElseThrow(() -> {
                        log.warn("Exam with given id does not exists [examId: {}]", editingExamResult.getExamId());
                        throw new ExamNotFoundException(ErrorDesc.EXAM_NOT_FOUND.getDesc());
                    });

            examResult.setExam(exam);
        }

        if (editingExamResult.getRemovedExamResultItems() != null
                && editingExamResult.getRemovedExamResultItems().size() > 0) {
            editingExamResult.getRemovedExamResultItems().forEach(removedExamResultItemId -> {
                ExamResultItem examResultItem = examResultItemRepository.findById(removedExamResultItemId)
                        .orElseThrow(() -> {
                            log.warn("Exam Result Item with given id does not exists [examResultItemId: {}]",
                                    removedExamResultItemId);
                            throw new ExamResultItemNotFoundException(ErrorDesc.EXAM_RESULT_ITEM_NOT_FOUND.getDesc());
                        });

                examResult.removeExamResultItem(examResultItem);
            });
        }

        if (editingExamResult.getAddedExamResultItems() != null
                && editingExamResult.getAddedExamResultItems().size() > 0) {
            editingExamResult.getAddedExamResultItems().forEach(creatingExamResultItemDto -> {
                ExamResultItem examResultItem = new ExamResultItem();
                examResultItem.setResultData(creatingExamResultItemDto.getResultData());
                examResultItem.setSortable(creatingExamResultItemDto.getSortable());

                if (creatingExamResultItemDto.getStudentId() != null) {
                    UUID studentId = UUID.fromString(creatingExamResultItemDto.getStudentId());

                    userRepository
                            .findById(studentId)
                            .ifPresent(user -> examResultItem.setStudent((Student) user));
                }

                if (creatingExamResultItemDto.getClassRoomId() != null) {
                    classroomRepository.findById(creatingExamResultItemDto.getClassRoomId())
                            .ifPresent(examResultItem::setClassroom);
                }

                examResult.addExamResultItem(examResultItem);
            });
        }

        examResultRepository.save(examResult);

        log.info("The Exam Result has been updated successfully [examResultId: {}, performedBy: {}]",
                examResultId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void updateExamResultItem(Long examResultItemId, EditingExamResultItemDto editingExamResultItem) {
        ExamResultItem examResultItem = examResultItemRepository.findById(examResultItemId)
                .orElseThrow(() -> {
                    log.warn("Exam Result Item with given id does not exists [examResultItemId: {}]", examResultItemId);
                    throw new ExamResultItemNotFoundException(ErrorDesc.EXAM_RESULT_ITEM_NOT_FOUND.getDesc());
                });

        if (editingExamResultItem.getSortable() != null) {
            examResultItem.setSortable(editingExamResultItem.getSortable());
        }

        if (editingExamResultItem.getResultData() != null
                && editingExamResultItem.getResultData().size() > 0) {
            examResultItem.setResultData(editingExamResultItem.getResultData());
        }

        if (editingExamResultItem.getExamResultId() != null) {
            ExamResult examResult = examResultRepository.findById(editingExamResultItem.getExamResultId())
                    .orElseThrow(() -> {
                        log.warn("Exam Result with given id does not exists [examResultId: {}]",
                                editingExamResultItem.getExamResultId());

                        throw new ExamResultNotFoundException(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc());
                    });

            examResultItem.setExamResult(examResult);
        }

        if (editingExamResultItem.getClassRoomId() != null) {
            Classroom classroom = classroomRepository.findById(editingExamResultItem.getClassRoomId())
                    .orElseThrow(() -> {
                        log.warn("Classroom with given id does not exists [classRoomId: {}]",
                                editingExamResultItem.getClassRoomId());
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            examResultItem.setClassroom(classroom);
        }

        if (editingExamResultItem.getStudentId() != null) {
            Student student = (Student) userRepository.findById(UUID.fromString(editingExamResultItem.getStudentId()))
                    .orElseThrow(() -> {
                        log.warn("Student with given id does not exists [studentId: {}]",
                                editingExamResultItem.getStudentId());
                        throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                    });

            examResultItem.setStudent(student);
        }

        examResultItemRepository.save(examResultItem);

        log.info("The Exam Result Item has been updated successfully [examResultItemId: {}, performedBy: {}]",
                examResultItemId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('delete:exam'))")
    @Override
    public void deleteExam(Long examId) {
        if (!examRepository.existsById(examId)) {
            log.warn("Exam with given id does not exists [examId: {}]", examId);
            throw new ExamNotFoundException(ErrorDesc.EXAM_NOT_FOUND.getDesc());
        }

        examRepository.deleteById(examId);

        log.info("The Exam has been deleted successfully [examID: {}, performedBy: {}]",
                examId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('delete:exam'))")
    @Override
    public void deleteExamType(Long examTypeId) {
        ExamType examType = examTypeRepository.findById(examTypeId)
                .orElseThrow(() -> {
                    log.warn("Exam Type with given id does not exists [examTypeId: {}]", examTypeId);
                    throw new ExamTypeNotFoundException(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc());
                });

        examType.getExams().forEach(exam -> exam.setExamType(null));

        examTypeRepository.deleteById(examTypeId);

        log.info("The Exam Type has been deleted successfully [examTypeId: {}, performedBy: {}]",
                examTypeId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('delete:exam'))")
    @Override
    public void deleteExamSkeleton(Long examSkeletonId) {
        ExamSkeleton examSkeleton = examSkeletonRepository.findById(examSkeletonId)
                .orElseThrow(() -> {
                    log.warn("Exam Skeleton with given id does not exists [examSkeletonId: {}]", examSkeletonId);
                    throw new ExamSkeletonNotFoundException(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc());
                });

        examSkeleton.getExams().forEach(exam -> exam.setExamSkeleton(null));

        examSkeletonRepository.deleteById(examSkeletonId);

        log.info("The Exam Skeleton has been deleted successfully [examSkeleton: {}, performedBy: {}]",
                examSkeletonId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:exams') || hasAuthority('edit:exam'))")
    @Override
    public void deleteExamResult(Long examResultId) {
        if (!examResultRepository.existsById(examResultId)) {
            log.warn("Exam Result with given id does not exists [examResultId: {}]", examResultId);
            throw new ExamResultNotFoundException(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc());
        }

        examResultRepository.deleteById(examResultId);

        log.info("The Exam Result has been deleted successfully [examResultId: {}, performedBy: {}]",
                examResultId, SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

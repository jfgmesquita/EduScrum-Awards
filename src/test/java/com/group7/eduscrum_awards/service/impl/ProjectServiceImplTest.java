package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentTaskDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Task;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProjectServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of its repositories.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock 
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;
    
    private Course existingCourse;
    private ProjectCreateDTO createDTO;
    private Project savedProject;
    private Student student;

    @BeforeEach
    void setUp() {
        // Setup base data
        existingCourse = new Course("Test Course");
        existingCourse.setId(1L);

        createDTO = new ProjectCreateDTO();
        createDTO.setName("Test Project");
        createDTO.setDescription("Desc");
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusMonths(1));

        savedProject = new Project(createDTO.getName(), createDTO.getDescription(), existingCourse, createDTO.getStartDate(), createDTO.getEndDate());
        savedProject.setId(10L);

        student = new Student("John", "john@test.com", "pass");
        student.setId(5L);
    }

    // Tests for createProject

    @Test
    @DisplayName("createProject | Should create project successfully")
    void testCreateProject_Success() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.empty());
        doReturn(savedProject).when(projectRepository).save(notNull());

        ProjectDTO result = projectService.createProject(1L, createDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Project", result.getName());
        
        // Validate with data defined in setUp() ("Desc")
        assertEquals("Desc", result.getDescription());
        
        // Validate with the date defined in setUp() (LocalDate.now())
        assertEquals(createDTO.getStartDate(), result.getStartDate());
        assertEquals(1L, result.getCourseId());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("createProject | Should throw ResourceNotFoundException when Course not found")
    void testCreateProject_Failure_CourseNotFound() {

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("Course not found with id: 1", exception.getMessage());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, never()).findByNameAndCourse(anyString(), any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProject | Should throw DuplicateResourceException when Project name exists in Course")
    void testCreateProject_Failure_DuplicateName() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.of(savedProject));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("A Project with the name 'Test Project' already exists in this course.", exception.getMessage());
        
        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, never()).save(any());
    }

    // Tests for getMyProjects

    @Test
    @DisplayName("getMyProjects | Should return filtered projects and tasks for the student")
    void testGetMyProjects_Success() {
        // 1. Arrange: Preparar IDs e Dados
        Long studentId = 5L;
        
        // Garantir que o aluno configurado no setUp() tem o ID correto
        student.setId(studentId);

        // Configurar o Mock do UserRepository para devolver o aluno (findById)
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));

        // 2. Arrange: Criar estrutura de Tarefas para testar o filtro
        Sprint sprint = new Sprint(1, "Sprint Goal", LocalDate.now(), LocalDate.now().plusDays(10), savedProject);
        sprint.setId(50L);

        // --- Tarefa do Aluno (Deve aparecer) ---
        TeamMember memberJohn = new TeamMember();
        memberJohn.setStudent(student); // Associar ao aluno do teste (ID 5)
        
        Task taskJohn = new Task("Task Title John", sprint);
        taskJohn.setId(101L);
        taskJohn.setDescription("Description John");
        taskJohn.setTeamMember(memberJohn);
        taskJohn.setStatus(com.group7.eduscrum_awards.model.enums.TaskStatus.TODO); // Ajusta o Enum se necessário

        // --- Tarefa de Outro Aluno (NÃO deve aparecer) ---
        Student otherStudent = new Student("Jane", "jane@test.com", "pass");
        otherStudent.setId(99L);
        
        TeamMember memberJane = new TeamMember();
        memberJane.setStudent(otherStudent); // Associar a outro aluno

        Task taskJane = new Task("Task Title Jane", sprint);
        taskJane.setId(102L);
        taskJane.setDescription("Description Jane");
        taskJane.setTeamMember(memberJane);
        taskJane.setStatus(com.group7.eduscrum_awards.model.enums.TaskStatus.DOING);

        // Adicionar ambas as tarefas à Sprint e a Sprint ao Projeto
        sprint.setTasks(new HashSet<>(Set.of(taskJohn, taskJane)));
        savedProject.setSprints(new HashSet<>(Set.of(sprint)));

        // Configurar o Mock do ProjectRepository
        when(projectRepository.findProjectsByStudentId(studentId)).thenReturn(List.of(savedProject));

        // 3. Act: Executar o serviço
        List<StudentProjectDTO> result = projectService.getMyProjects(studentId);

        // 4. Assert: Verificações
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        assertEquals(1, result.get(0).getSprints().size());

        // AQUI É A PARTE CRÍTICA:
        // A lista de tarefas dentro da sprint deve ter tamanho 1 (apenas a do John)
        List<StudentTaskDTO> studentTasks = result.get(0).getSprints().get(0).getTasks();
        
        assertEquals(1, studentTasks.size(), "Deve conter apenas as tarefas atribuídas ao aluno");
        
        // Validar que a tarefa que veio é a correta
        assertEquals(101L, studentTasks.get(0).getId());
        assertEquals("Description John", studentTasks.get(0).getDescription());
        
        // Validar que o método de repositório correto foi chamado
        verify(projectRepository, times(1)).findProjectsByStudentId(studentId);
    }

    @Test
    @DisplayName("getMyProjects | Should throw exception if student not found")
    void testGetMyProjects_StudentNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getMyProjects(99L));
        
        verify(projectRepository, never()).findProjectsByStudentId(any());
    }
}
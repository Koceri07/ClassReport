package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.ParentEntity
import com.classreport.classreport.entity.StudentEntity
import com.classreport.classreport.entity.TeacherEntity
import com.classreport.classreport.entity.UserEntity
import com.classreport.classreport.model.enums.Role
import com.classreport.classreport.model.request.RefreshTokenRequest
import com.classreport.classreport.model.request.UserRequest
import com.classreport.classreport.model.response.ApiResponse
import com.classreport.classreport.model.response.AuthResponse
import com.classreport.classreport.model.response.UserResponse
import com.classreport.classreport.repository.ParentRepository
import com.classreport.classreport.repository.StudentRepository
import com.classreport.classreport.repository.TeacherRepository
import com.classreport.classreport.repository.UserRepository
import com.classreport.classreport.service.AuthService
import com.classreport.classreport.service.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

import java.util.Optional

class AuthServiceTest extends Specification {

    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def jwtService = Mock(JwtService)
    def authenticationManager = Mock(AuthenticationManager)
    def studentRepository = Mock(StudentRepository)
    def teacherRepository = Mock(TeacherRepository)
    def parentRepository = Mock(ParentRepository)

    @Subject
    def authService = new AuthService(
            userRepository, passwordEncoder, jwtService, authenticationManager,
            studentRepository, teacherRepository, parentRepository
    )

    def userRequest
    def teacherEntity
    def studentEntity
    def parentEntity
    def userEntity

    def setup() {
        userRequest = new UserRequest()
        userRequest.setId(1L)
        userRequest.setEmail("test@example.com")
        userRequest.setPassword("password123")
        userRequest.setName("Test")
        userRequest.setSurname("User")
        userRequest.setRole(Role.TEACHER)

        teacherEntity = new TeacherEntity()
        teacherEntity.setId(1L)
        teacherEntity.setEmail("test@example.com")
        teacherEntity.setPassword("encodedPassword")
        teacherEntity.setName("Test")
        teacherEntity.setSurname("User")
        teacherEntity.setRole(Role.TEACHER)
        teacherEntity.setActive(true)

        studentEntity = new StudentEntity()
        studentEntity.setId(1L)
        studentEntity.setEmail("student@example.com")
        studentEntity.setPassword("encodedPassword")
        studentEntity.setName("Student")
        studentEntity.setSurname("Test")
        studentEntity.setRole(Role.STUDENT)
        studentEntity.setActive(true)

        parentEntity = new ParentEntity()
        parentEntity.setId(1L)
        parentEntity.setEmail("parent@example.com")
        parentEntity.setPassword("encodedPassword")
        parentEntity.setName("Parent")
        parentEntity.setSurname("Test")
        parentEntity.setRole(Role.PARENT)
        parentEntity.setActive(true)

        userEntity = teacherEntity
    }

    def "register should create teacher successfully"() {
        given:
        userRequest.setRole(Role.TEACHER)

        when:
        def result = authService.register(userRequest)

        then:
        1 * teacherRepository.existsByEmail(userRequest.getEmail()) >> false
        1 * passwordEncoder.encode(userRequest.getPassword()) >> "encodedPassword"
        1 * teacherRepository.save(_) >> { TeacherEntity savedTeacher ->
            assert savedTeacher.email == userRequest.getEmail()
            assert savedTeacher.password == "encodedPassword"
            assert savedTeacher.role == Role.TEACHER
            savedTeacher
        }
        1 * jwtService.generateToken(_) >> "jwtToken"

        result.code == "200"
        result.message == "Successfully registered"
        result.data != null
        result.data.token == "jwtToken"
        result.data.role == "TEACHER"
    }

    def "register should create student successfully"() {
        given:
        userRequest.setRole(Role.STUDENT)

        when:
        def result = authService.register(userRequest)

        then:
        1 * studentRepository.existsByEmail(userRequest.getEmail()) >> false
        1 * passwordEncoder.encode(userRequest.getPassword()) >> "encodedPassword"
        1 * studentRepository.save(_) >> { StudentEntity savedStudent ->
            assert savedStudent.email == userRequest.getEmail()
            assert savedStudent.password == "encodedPassword"
            assert savedStudent.role == Role.STUDENT
            savedStudent
        }
        1 * jwtService.generateToken(_) >> "jwtToken"

        result.code == "200"
        result.data.role == "STUDENT"
    }

    def "register should create parent successfully"() {
        given:
        userRequest.setRole(Role.PARENT)

        when:
        def result = authService.register(userRequest)

        then:
        1 * parentRepository.existsByEmail(userRequest.getEmail()) >> false
        1 * passwordEncoder.encode(userRequest.getPassword()) >> "encodedPassword"
        1 * parentRepository.save(_) >> { ParentEntity savedParent ->
            assert savedParent.email == userRequest.getEmail()
            assert savedParent.password == "encodedPassword"
            assert savedParent.role == Role.PARENT
            savedParent
        }
        1 * jwtService.generateToken(_) >> "jwtToken"

        result.code == "200"
        result.data.role == "PARENT"
    }

    def "register should return error when email already exists"() {
        given:
        userRequest.setRole(Role.TEACHER)

        when:
        def result = authService.register(userRequest)

        then:
        1 * teacherRepository.existsByEmail(userRequest.getEmail()) >> true
        0 * passwordEncoder.encode(_)
        0 * teacherRepository.save(_)

        // ApiResponse həmişə code=200 qaytarır, error data field-də olur
        result.code == "200"
        result.data == "400"
    }

    def "register should return error when required fields are null"() {
        given:
        def invalidRequest = new UserRequest()
        // Email, password, role null

        when:
        def result = authService.register(invalidRequest)

        then:
        0 * teacherRepository.existsByEmail(_)
        0 * passwordEncoder.encode(_)
        0 * teacherRepository.save(_)

        result.code == "200"
        result.data == "400"
    }

    def "register should return error when password is empty"() {
        given:
        userRequest.setPassword("   ")

        when:
        def result = authService.register(userRequest)

        then:
        0 * teacherRepository.existsByEmail(_)
        0 * passwordEncoder.encode(_)
        0 * teacherRepository.save(_)

        result.code == "200"
        result.data == "400"
    }

    def "register should return error for invalid role"() {
        given:
        userRequest.setRole(null)

        when:
        def result = authService.register(userRequest)

        then:
        0 * teacherRepository.existsByEmail(_)
        0 * passwordEncoder.encode(_)
        0 * teacherRepository.save(_)

        result.code == "200"
        result.data == "400"
    }

    def "login should return success for valid teacher credentials"() {
        given:
        userRequest.setEmail("teacher@example.com")

        when:
        def result = authService.login(userRequest)

        then:
        1 * teacherRepository.findByEmail(userRequest.getEmail()) >> teacherEntity
        1 * passwordEncoder.matches(userRequest.getPassword(), teacherEntity.getPassword()) >> true
        1 * jwtService.generateToken(teacherEntity) >> "jwtToken"

        result.code == "200"
        result.message == "Successfully logged in"
        result.data.token == "jwtToken"
        result.data.role == "TEACHER"
    }

    def "login should return error when user not found"() {
        when:
        def result = authService.login(userRequest)

        then:
        1 * teacherRepository.findByEmail(userRequest.getEmail()) >> null
        1 * studentRepository.findByEmail(userRequest.getEmail()) >> null
        1 * parentRepository.findByEmail(userRequest.getEmail()) >> null
        0 * passwordEncoder.matches(_, _)

        result.code == "200"
        result.data == "404"
    }

    def "login should return error for invalid password"() {
        when:
        def result = authService.login(userRequest)

        then:
        1 * teacherRepository.findByEmail(userRequest.getEmail()) >> teacherEntity
        1 * passwordEncoder.matches(userRequest.getPassword(), teacherEntity.getPassword()) >> false

        result.code == "200"
        result.data == "401"
    }

    def "login should return error when email or password is null"() {
        given:
        def invalidRequest = new UserRequest()
        // Email və password null

        when:
        def result = authService.login(invalidRequest)

        then:
        0 * teacherRepository.findByEmail(_)
        0 * passwordEncoder.matches(_, _)

        result.code == "200"
        result.data == "400"
    }

    def "refreshToken should return new token for valid refresh token"() {
        given:
        def authHeader = "Bearer validRefreshToken"
        def refreshTokenRequest = new RefreshTokenRequest()
        refreshTokenRequest.setRefreshToken("validRefreshToken")

        when:
        def result = authService.refreshToken(refreshTokenRequest)

        then:
        1 * jwtService.isRefreshTokenValid("validRefreshToken") >> true
        1 * jwtService.extractUsername("validRefreshToken") >> "test@example.com"
        1 * userRepository.findByEmail("test@example.com") >> Optional.of(userEntity)
        1 * jwtService.generateToken(userEntity) >> "newAccessToken"
        1 * jwtService.generateRefreshToken(userEntity) >> "newRefreshToken"

        result instanceof AuthResponse
        result.accessToken == "newAccessToken"
        result.refreshToken == "newRefreshToken"
    }

    def "refreshToken should throw exception for invalid refresh token"() {
        given:
        def refreshTokenRequest = new RefreshTokenRequest()
        refreshTokenRequest.setRefreshToken("invalidToken")

        when:
        authService.refreshToken(refreshTokenRequest)

        then:
        1 * jwtService.isRefreshTokenValid("invalidToken") >> false
        thrown(RuntimeException)
    }

    def "refreshToken should throw exception when user not found"() {
        given:
        def refreshTokenRequest = new RefreshTokenRequest()
        refreshTokenRequest.setRefreshToken("validToken")

        when:
        authService.refreshToken(refreshTokenRequest)

        then:
        1 * jwtService.isRefreshTokenValid("validToken") >> true
        1 * jwtService.extractUsername("validToken") >> "nonexistent@example.com"
        1 * userRepository.findByEmail("nonexistent@example.com") >> Optional.empty()
        thrown(RuntimeException)
    }

    def "getCurrentUser should return user data for valid token"() {
        given:
        def authHeader = "Bearer validToken"

        when:
        def result = authService.getCurrentUser(authHeader)

        then:
        1 * jwtService.extractTokenFromHeader(authHeader) >> "validToken"
        1 * jwtService.extractUsername("validToken") >> "test@example.com"
        1 * userRepository.findByEmail("test@example.com") >> Optional.of(userEntity)

        result instanceof ApiResponse
        result.data != null
        result.data.id == userEntity.getId()
        result.data.email == userEntity.getEmail()
    }

    def "getCurrentUser should return error when token not provided"() {
        given:
        def authHeader = "InvalidHeader"

        when:
        def result = authService.getCurrentUser(authHeader)

        then:
        1 * jwtService.extractTokenFromHeader(authHeader) >> null

        result instanceof ApiResponse
        result.data == "Token not provided"
    }

    def "getCurrentUser should return error when user not found"() {
        given:
        def authHeader = "Bearer validToken"

        when:
        def result = authService.getCurrentUser(authHeader)

        then:
        1 * jwtService.extractTokenFromHeader(authHeader) >> "validToken"
        1 * jwtService.extractUsername("validToken") >> "nonexistent@example.com"
        1 * userRepository.findByEmail("nonexistent@example.com") >> Optional.empty()

        result instanceof ApiResponse
        result.data == "User not found"
    }

    def "logout should return success"() {
        given:
        def authHeader = "Bearer token"

        when:
        def result = authService.logout(authHeader)

        then:
        result.code == "200"
    }

    def "createTokensForUser should return tokens for valid user"() {
        given:
        def email = "test@example.com"

        when:
        def result = authService.createTokensForUser(email)

        then:
        1 * userRepository.findSimpleByEmail(email) >> Optional.of(userEntity)
        1 * jwtService.generateToken(userEntity) >> "accessToken"
        1 * jwtService.generateRefreshToken(userEntity) >> "refreshToken"

        result instanceof AuthResponse
        result.accessToken == "accessToken"
        result.refreshToken == "refreshToken"
    }

    def "createTokensForUser should throw exception when user not found"() {
        given:
        def email = "nonexistent@example.com"

        when:
        authService.createTokensForUser(email)

        then:
        1 * userRepository.findSimpleByEmail(email) >> Optional.empty()
        thrown(RuntimeException)
    }

    def "findUserByEmail should return teacher when exists"() {
        when:
        def result = authService.findUserByEmail("teacher@example.com")

        then:
        1 * teacherRepository.findByEmail("teacher@example.com") >> teacherEntity
        0 * studentRepository.findByEmail(_)
        0 * parentRepository.findByEmail(_)

        result == teacherEntity
    }

    def "findUserByEmail should return student when teacher not exists"() {
        when:
        def result = authService.findUserByEmail("student@example.com")

        then:
        1 * teacherRepository.findByEmail("student@example.com") >> null
        1 * studentRepository.findByEmail("student@example.com") >> studentEntity
        0 * parentRepository.findByEmail(_)

        result == studentEntity
    }

    def "findUserByEmail should return parent when teacher and student not exist"() {
        when:
        def result = authService.findUserByEmail("parent@example.com")

        then:
        1 * teacherRepository.findByEmail("parent@example.com") >> null
        1 * studentRepository.findByEmail("parent@example.com") >> null
        1 * parentRepository.findByEmail("parent@example.com") >> parentEntity

        result == parentEntity
    }

    def "findUserByEmail should return null when no user exists"() {
        when:
        def result = authService.findUserByEmail("nonexistent@example.com")

        then:
        1 * teacherRepository.findByEmail("nonexistent@example.com") >> null
        1 * studentRepository.findByEmail("nonexistent@example.com") >> null
        1 * parentRepository.findByEmail("nonexistent@example.com") >> null

        result == null
    }
}
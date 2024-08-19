package org.example.library.services;

import org.example.library.entities.Book;
import org.example.library.entities.Member;
import org.example.library.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMemberTest() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Member");

        when(memberRepository.save(member)).thenReturn(member);
        Member result = memberService.saveMember(member);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(member);
    }


    @Test
    void getExistedMemberByIdTest() {
        Member expectedMember = new Member();
        expectedMember.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(expectedMember));
        Member actualMember = memberService.getMemberById(1L);

        assertNotNull(actualMember);
        assertEquals(expectedMember, actualMember);
    }

    @Test
    void getNotExistedMemberByIdTes() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            memberService.getMemberById(1L);
        });

        assertEquals("No member with such id", exception.getMessage());
    }


    @Test
    void updateExistedMemberTest() {
        Member existedMember = new Member();
        existedMember.setId(1L);
        existedMember.setName("Member");


        Member dataMember = new Member();
        dataMember.setId(2L);
        dataMember.setName("Updated member");

        when(memberRepository.findById(existedMember.getId())).thenReturn(Optional.of(existedMember));


        existedMember = memberService.updateById(1L, dataMember);

        assertNotEquals(existedMember.getId(), dataMember.getId());
        assertEquals(existedMember.getName(), dataMember.getName());

    }

    @Test
    void updateNotExistedBookTest() {
        Member dataMember = new Member();
        dataMember.setId(2L);
        dataMember.setName("Updated member");

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> memberService.updateById(1L, dataMember));
        assertEquals(exception.getMessage(), "No member with such id");
    }

    @Test
    void deleteExistedMemberTest() {
        Member existedMember = new Member();
        existedMember.setId(1L);
        existedMember.setName("John Doe");

        when(memberRepository.findById(existedMember.getId())).thenReturn(Optional.of(existedMember));

        assertDoesNotThrow(() -> memberService.deleteById(existedMember.getId()));

    }

    @Test
    void deleteNotExistedMemberTest() {

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> memberService.deleteById(1L));
        assertEquals("No member with such id", exception.getMessage());
    }

    @Test
    void deleteExistedMemberWithBorrowedBooksTest() {

        Member existedMember = new Member();
        existedMember.setId(1L);
        existedMember.setName("John Doe");
        Book borrowedBook = new Book();
        borrowedBook.setId(1L);
        existedMember.getBooks().add(borrowedBook);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existedMember));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.deleteById(1L));
        assertEquals("Member cannot be deleted because he borrowed books.", exception.getMessage());
    }

}

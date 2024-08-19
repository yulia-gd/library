package org.example.library.services;

import lombok.RequiredArgsConstructor;
import org.example.library.entities.Member;
import org.example.library.repositories.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No member with such id"));
    }

    @Transactional
    public Member updateById(Long id, Member member) {
        Member existedMember = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No member with such id"));
        existedMember.update(member);
        return existedMember;
    }

    @Transactional
    public void deleteById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No member with such id"));
        if (!member.getBooks().isEmpty()) {
            throw new IllegalStateException("Member cannot be deleted because he borrowed books.");
        }
        memberRepository.deleteById(id);
    }
}

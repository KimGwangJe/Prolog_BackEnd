package com.prolog.prologbackend.Security.UserDetails;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> {throw new UsernameNotFoundException("해당 이메일과 일치하는 회원이 없습니다.");});
        return new CustomUserDetails(member);
    }
}

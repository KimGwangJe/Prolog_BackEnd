package com.prolog.prologbackend.Security.UserDetails;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import com.prolog.prologbackend.Security.ExceptionType.SecurityExceptionType;
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
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(SecurityExceptionType.NOT_FOUND.getErrorMessage());
                });
        return new CustomUserDetails(member);
    }
}

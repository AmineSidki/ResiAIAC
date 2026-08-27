package org.aminesidki.resiaiac.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfiguration {
  // this bean is set static because it is needed far earlier than the others,
  // before the other configurations are confirmed
  @Bean
  static RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
        """
        ROLE_ADMINISTRATEUR > ROLE_RESPONSABLE
        ROLE_RESPONSABLE > ROLE_MANAGER
        ROLE_MANAGER > ROLE_ETUDIANT
        """);
  }
}
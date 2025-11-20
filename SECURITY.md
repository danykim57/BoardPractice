# Security Configuration Guide

## Overview

This document describes the security features implemented in BoardPractice and how to configure them properly for development and production environments.

## Recent Security Improvements

All critical security vulnerabilities have been addressed:

1. ✅ **Environment-based Configuration** - Sensitive credentials now use environment variables
2. ✅ **SSL/TLS Support** - Database connections support SSL encryption
3. ✅ **Redis Security** - Password authentication support for Redis
4. ✅ **Security Headers** - Enabled protection against clickjacking, XSS, and MIME-sniffing
5. ✅ **CSRF Protection** - Properly configured for hybrid JWT/session authentication
6. ✅ **Authentication & Authorization** - Protected write operations require authentication

## Environment Variables Configuration

### Quick Start

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your configuration:
   ```bash
   nano .env  # or use your preferred editor
   ```

3. **IMPORTANT**: Never commit the `.env` file to version control!

### Available Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database server hostname | `localhost` | No |
| `DB_PORT` | Database server port | `3306` | No |
| `DB_NAME` | Database name | `board` | No |
| `DB_USERNAME` | Database username | `root` | Yes* |
| `DB_PASSWORD` | Database password | `zen911!@` | Yes* |
| `DB_USE_SSL` | Enable SSL for database | `false` | No |
| `REDIS_HOST` | Redis server hostname | `localhost` | No |
| `REDIS_PORT` | Redis server port | `6379` | No |
| `REDIS_PASSWORD` | Redis password | _(empty)_ | No |

*Required in production. Defaults provided for local development only.

## Security Features

### 1. CSRF Protection

**Status**: ✅ Enabled (with exceptions for stateless APIs)

CSRF protection is enabled for session-based endpoints (`.do` URLs) and disabled for stateless REST APIs (`/api/**`, `*.json`).

**Protected Endpoints**:
- `/post/write.do`
- `/post/save.do`
- `/post/update.do`
- `/post/delete.do`

**CSRF-Exempt Endpoints** (using JWT):
- `/api/**`
- `/**/*.json`
- `/members`

### 2. Security Headers

**Status**: ✅ Enabled

The following security headers are automatically added to all responses:

- **X-Frame-Options**: `DENY` - Prevents clickjacking attacks
- **X-Content-Type-Options**: `nosniff` - Prevents MIME-sniffing
- **X-XSS-Protection**: `1; mode=block` - Enables XSS filtering
- **Strict-Transport-Security**: `max-age=31536000; includeSubDomains` - Enforces HTTPS

### 3. Authentication & Authorization

**Status**: ✅ Implemented

**Public Endpoints** (no authentication required):
- `/` - Home page
- `/post/list.do` - View post list
- `/post/view.do` - View individual posts
- `/members` - Test endpoint
- `/api/session/create` - Create session (login)
- Static resources (`/static/**`, `/css/**`, `/js/**`)

**Protected Endpoints** (authentication required):
- `/post/write.do` - Create new post
- `/post/save.do` - Save post
- `/post/save.json` - Save post (REST API)
- `/post/update.do` - Update post
- `/post/delete.do` - Delete post
- `/api/session/info` - Get session info
- `/api/session/logout` - Logout
- `/api/session/extend` - Extend session

### 4. Hybrid Authentication

The application supports both:
- **Session-based authentication** (for traditional web forms)
- **JWT token authentication** (for REST APIs)

Session data is stored in Redis for scalability and persistence.

## Production Deployment Checklist

Before deploying to production, complete the following security tasks:

### Database Security

- [ ] Create a dedicated database user (not `root`)
  ```sql
  CREATE USER 'boardapp'@'%' IDENTIFIED BY 'strong-password-here';
  GRANT SELECT, INSERT, UPDATE, DELETE ON board.* TO 'boardapp'@'%';
  FLUSH PRIVILEGES;
  ```
- [ ] Set `DB_USERNAME=boardapp`
- [ ] Set strong `DB_PASSWORD` (minimum 16 characters, mixed case, numbers, symbols)
- [ ] Enable SSL: Set `DB_USE_SSL=true`
- [ ] Configure MySQL SSL certificates
- [ ] Restrict database network access (firewall rules)

### Redis Security

- [ ] Edit `/etc/redis/redis.conf` and set:
  ```
  requirepass your-strong-redis-password
  bind 127.0.0.1
  ```
- [ ] Restart Redis: `sudo systemctl restart redis`
- [ ] Set `REDIS_PASSWORD=your-strong-redis-password`
- [ ] Enable Redis persistence (RDB or AOF)
- [ ] Restrict Redis network access (firewall rules)

### Application Security

- [ ] Deploy behind HTTPS (use nginx/Apache as reverse proxy with SSL certificates)
- [ ] Set all environment variables via system environment (not `.env` file in production)
- [ ] Remove default values from production configuration
- [ ] Enable application-level logging for security events
- [ ] Configure rate limiting (e.g., nginx `limit_req_zone`)
- [ ] Set up Web Application Firewall (WAF) if available
- [ ] Review and update `anyRequest().permitAll()` to `.authenticated()` if all endpoints should be protected

### Infrastructure Security

- [ ] Use a secrets management system (AWS Secrets Manager, HashiCorp Vault, etc.)
- [ ] Enable automated security scanning (Dependabot, Snyk, etc.)
- [ ] Set up monitoring and alerting for security events
- [ ] Configure regular database backups
- [ ] Implement log rotation and centralized logging
- [ ] Regular security updates and patching schedule

## Development Environment

For local development, the application works with default settings:

```bash
# Start MySQL (default port 3306, no SSL)
mysql -u root -p

# Start Redis (default port 6379, no password)
redis-server

# Run application
./gradlew bootRun
```

**Note**: Development defaults are insecure and should NEVER be used in production.

## Security Best Practices

### 1. Never Commit Secrets

- ✅ Use `.env` files for local development
- ✅ Add `.env` to `.gitignore`
- ✅ Use environment variables in production
- ❌ Never hardcode passwords, tokens, or keys

### 2. Least Privilege Principle

- Create dedicated users with minimal required permissions
- Avoid using `root` or admin accounts for applications
- Regularly review and audit permissions

### 3. Defense in Depth

- Use multiple layers of security (SSL, authentication, authorization, headers)
- Validate input at multiple levels (client, server, database)
- Log security events for monitoring and forensics

### 4. Keep Dependencies Updated

```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Update dependencies regularly
# Review security advisories
```

## Reporting Security Issues

If you discover a security vulnerability, please:

1. **DO NOT** create a public GitHub issue
2. Email security concerns to the maintainer
3. Include detailed steps to reproduce
4. Allow time for a fix before public disclosure

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [Redis Security Guide](https://redis.io/docs/management/security/)
- [MySQL Security Best Practices](https://dev.mysql.com/doc/refman/8.0/en/security-guidelines.html)

---

**Last Updated**: 2025-11-20
**Security Version**: 1.0

insert into roles (name)
values ('ROLE_ADMIN');
insert into roles (name)
values ('ROLE_PARTICIPANT');

insert into privileges_categories (id, name) values (0,'OVER_USERS');
insert into privileges_categories (id, name) values (1,'OVER_STATISTICS');
insert into privileges_categories (id, name) values (2,'OVER_EVENTS');
insert into privileges (name, category_id) values ('SEARCH_USERS', 0);
insert into privileges (name, category_id) values ('BAN_USERS', 0);
insert into privileges (name, category_id) values ('UNBAN_USERS', 0);
insert into privileges (name, category_id) values ('REGISTER_ADMIN', 0);
insert into privileges (name, category_id) values ('READ_ADMINS', 0);
insert into privileges (name, category_id) values ('MODIFY_ADMIN', 0);
insert into privileges (name, category_id) values ('DELETE_ADMIN', 0);
insert into privileges (name, category_id) values ('GENERATE_STATISTICS', 1);
insert into privileges (name, category_id) values ('READ_STATISTICS', 1);
insert into privileges (name, category_id) values ('SEARCH_EVENTS', 2);
insert into privileges (name, category_id) values ('CANCEL_EVENTS', 2);

insert into activities (id, name) values ( 'f0862697-ce12-4f92-9a4b-97ae672b8ad0', 'futbol' );
insert into activities (id, name) values ( '508ce32f-39ad-4a99-bfc2-19408ceb7376', 'tennis' );
insert into activities (id, name) values ( '832bc5b7-d927-4945-93f7-7bf3957d3073', 'futbol americano' );
insert into activities (id, name) values ( 'df6d1908-9bf9-4d15-aad8-7414883d04c4', 'futbol sala' );
insert into activities (id, name) values ( 'd79e17f0-3ad5-4896-935c-c8dd4324f00e', 'básquet' );

-- Mocked participant user. Password=mockpass
insert into users (id, name, last_name, email, birthdate, password, account_non_expired,
                   credentials_non_expired, account_non_locked, enabled)
values ('a1b1488c-b222-4a01-b88e-7dc7ce7a58f8', 'mock', 'mocked', 'mockUser@email.com', '2001-10-31',
        '$2a$12$TRXJRcBWduilWn.VDonMk.EsLADjol79NBr0mDR.yaq3l13bIkmUu',
        true, true, true, true);

insert into users_roles (user_id, role_id) values ('a1b1488c-b222-4a01-b88e-7dc7ce7a58f8', 2);

insert into participants(id, reputation_as_participant, reputation_as_event_creator)
values ( 'a1b1488c-b222-4a01-b88e-7dc7ce7a58f8', 'GOOD', 'GOOD' );

-- Mocked participant user with liked mercado pago account. Password=mockpass
insert into users (id, name, last_name, email, birthdate, password, account_non_expired,
                   credentials_non_expired, account_non_locked, enabled)
values ('51ef4c13-ccbc-4054-904b-6f70e436f7d5', 'mpuser', 'mpuser', 'mpuser@email.com', '2001-10-31',
        '$2a$12$TRXJRcBWduilWn.VDonMk.EsLADjol79NBr0mDR.yaq3l13bIkmUu',
        true, true, true, true);

insert into users_roles (user_id, role_id) values ('51ef4c13-ccbc-4054-904b-6f70e436f7d5', 2);

insert into participants(id, reputation_as_participant, reputation_as_event_creator)
values ( '51ef4c13-ccbc-4054-904b-6f70e436f7d5', 'GOOD' , 'GOOD' );

insert into mp_auth_tokens(id, access_token, token_type, expire_in, user_code, scope, refresh_token, public_key)
values ('51ef4c13-ccbc-4054-904b-6f70e436f7d5',
        'APP_USR-7019845672515236-032609-73133eb62337f22abdb6612e0f34c262-1744739090',
        'bearer', 15552000, 1744739090, 'offline_access read write', 'TG-XXXXXXXX-241983636',
        'APP_USR-8aa919b3-634f-4ab7-af5f-51087dbd18f5');

update participants set mp_auth_token_id='51ef4c13-ccbc-4054-904b-6f70e436f7d5'
                    where id='51ef4c13-ccbc-4054-904b-6f70e436f7d5';

-- Departments
insert into departments (name) values ( 'Desarrollo');
-- Admin account.
insert into users (id, name, last_name, email, birthdate, password, account_non_expired,
                   credentials_non_expired, account_non_locked, enabled)
values ('eaa879c6-59e1-4ea7-950f-0063b70eed56', 'Fabio', 'Sager', 'fabio@email.com', '2001-10-31',
        '$2a$10$En1BgDKDvO8vMZIGiILWSeOavYiuxqTVjqdDfehyaLpJwDIbwOZye',
        true, true, true, true);

insert into users_roles (user_id, role_id) values ('eaa879c6-59e1-4ea7-950f-0063b70eed56', 1);
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 1 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 2 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 3 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 4 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 5 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 6 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 7 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 8 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 9 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 10 );
insert into admins_privileges(admin_id, privilege_id) values ( 'eaa879c6-59e1-4ea7-950f-0063b70eed56', 11 );

insert into admins (id, phone_number, department_id) values ('eaa879c6-59e1-4ea7-950f-0063b70eed56', '123456', 1);
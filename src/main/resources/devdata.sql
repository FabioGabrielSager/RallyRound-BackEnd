insert into roles (name)
values ('ROLE_ADMIN');
insert into roles (name)
values ('ROLE_PARTICIPANT');

insert into activities (id, name) values ( 'f0862697-ce12-4f92-9a4b-97ae672b8ad0', 'futbol' );
insert into activities (id, name) values ( '508ce32f-39ad-4a99-bfc2-19408ceb7376', 'tennis' );
insert into activities (id, name) values ( '832bc5b7-d927-4945-93f7-7bf3957d3073', 'futbol americano' );
insert into activities (id, name) values ( 'df6d1908-9bf9-4d15-aad8-7414883d04c4', 'futbol sala' );
insert into activities (id, name) values ( 'd79e17f0-3ad5-4896-935c-c8dd4324f00e', 'básquet' );

-- Mocked participant user
insert into users (id, name, last_name, email, birthdate, password, account_non_expired,
                   credentials_non_expired, account_non_locked, enabled)
values ('a1b1488c-b222-4a01-b88e-7dc7ce7a58f8', 'mock', 'mocked', 'mockUser@email.com', '2001-10-31',
        '$2a$12$TRXJRcBWduilWn.VDonMk.EsLADjol79NBr0mDR.yaq3l13bIkmUu',
        true, true, true, true);

insert into users_roles (user_id, role_id) values ('a1b1488c-b222-4a01-b88e-7dc7ce7a58f8', 2)
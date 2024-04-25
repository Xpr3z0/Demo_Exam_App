PGDMP         0                |            postgres    15.4    15.4 3    6           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            7           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            8           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            9           1262    5    postgres    DATABASE     |   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE postgres;
                postgres    false            :           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3385                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                pg_database_owner    false            ;           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   pg_database_owner    false    5            �            1259    33952    assignments    TABLE     �   CREATE TABLE public.assignments (
    id integer NOT NULL,
    id_request integer,
    member_id integer,
    is_responsible boolean
);
    DROP TABLE public.assignments;
       public         heap    postgres    false    5            �            1259    33951    assignments_id_seq    SEQUENCE     �   CREATE SEQUENCE public.assignments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.assignments_id_seq;
       public          postgres    false    222    5            <           0    0    assignments_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.assignments_id_seq OWNED BY public.assignments.id;
          public          postgres    false    221            �            1259    33945    members    TABLE     �   CREATE TABLE public.members (
    id integer NOT NULL,
    name character varying(100),
    login character varying(50),
    pass character varying(50),
    role character varying(50)
);
    DROP TABLE public.members;
       public         heap    postgres    false    5            �            1259    33944    members_id_seq    SEQUENCE     �   CREATE SEQUENCE public.members_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.members_id_seq;
       public          postgres    false    220    5            =           0    0    members_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.members_id_seq OWNED BY public.members.id;
          public          postgres    false    219            �            1259    33983    orders    TABLE     �   CREATE TABLE public.orders (
    id integer NOT NULL,
    request_id integer,
    resource_type character varying(100),
    resource_name character varying(255),
    cost numeric
);
    DROP TABLE public.orders;
       public         heap    postgres    false    5            �            1259    33982    orders_id_seq    SEQUENCE     �   CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          postgres    false    225    5            >           0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          postgres    false    224            �            1259    33970    reports    TABLE     �   CREATE TABLE public.reports (
    request_id integer NOT NULL,
    repair_type character varying(100),
    "time" integer,
    cost numeric,
    resources text,
    reason text,
    help text
);
    DROP TABLE public.reports;
       public         heap    postgres    false    5            �            1259    33934    request_processes    TABLE     �   CREATE TABLE public.request_processes (
    request_id integer NOT NULL,
    priority character varying(50),
    date_finish_plan date
);
 %   DROP TABLE public.request_processes;
       public         heap    postgres    false    5            �            1259    33924    request_regs    TABLE     �   CREATE TABLE public.request_regs (
    request_id integer NOT NULL,
    client_name character varying(100),
    client_phone character varying(20),
    date_start date
);
     DROP TABLE public.request_regs;
       public         heap    postgres    false    5            �            1259    33916    requests    TABLE     �   CREATE TABLE public.requests (
    id integer NOT NULL,
    equip_num character varying(255),
    equip_type character varying(255),
    problem_desc character varying(255),
    request_comments text,
    status character varying(50)
);
    DROP TABLE public.requests;
       public         heap    postgres    false    5            �            1259    33915    requests_id_seq    SEQUENCE     �   CREATE SEQUENCE public.requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.requests_id_seq;
       public          postgres    false    216    5            ?           0    0    requests_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;
          public          postgres    false    215            �           2604    33955    assignments id    DEFAULT     p   ALTER TABLE ONLY public.assignments ALTER COLUMN id SET DEFAULT nextval('public.assignments_id_seq'::regclass);
 =   ALTER TABLE public.assignments ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    221    222    222            �           2604    33948 
   members id    DEFAULT     h   ALTER TABLE ONLY public.members ALTER COLUMN id SET DEFAULT nextval('public.members_id_seq'::regclass);
 9   ALTER TABLE public.members ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    220    219    220            �           2604    33986 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    224    225    225            �           2604    33919    requests id    DEFAULT     j   ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);
 :   ALTER TABLE public.requests ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    216    215    216            0          0    33952    assignments 
   TABLE DATA           P   COPY public.assignments (id, id_request, member_id, is_responsible) FROM stdin;
    public          postgres    false    222   (:       .          0    33945    members 
   TABLE DATA           >   COPY public.members (id, name, login, pass, role) FROM stdin;
    public          postgres    false    220   E:       3          0    33983    orders 
   TABLE DATA           T   COPY public.orders (id, request_id, resource_type, resource_name, cost) FROM stdin;
    public          postgres    false    225   y;       1          0    33970    reports 
   TABLE DATA           a   COPY public.reports (request_id, repair_type, "time", cost, resources, reason, help) FROM stdin;
    public          postgres    false    223   �;       ,          0    33934    request_processes 
   TABLE DATA           S   COPY public.request_processes (request_id, priority, date_finish_plan) FROM stdin;
    public          postgres    false    218   �;       +          0    33924    request_regs 
   TABLE DATA           Y   COPY public.request_regs (request_id, client_name, client_phone, date_start) FROM stdin;
    public          postgres    false    217   �;       *          0    33916    requests 
   TABLE DATA           e   COPY public.requests (id, equip_num, equip_type, problem_desc, request_comments, status) FROM stdin;
    public          postgres    false    216   T=       @           0    0    assignments_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.assignments_id_seq', 1, false);
          public          postgres    false    221            A           0    0    members_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.members_id_seq', 8, true);
          public          postgres    false    219            B           0    0    orders_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.orders_id_seq', 1, false);
          public          postgres    false    224            C           0    0    requests_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.requests_id_seq', 10, true);
          public          postgres    false    215            �           2606    33957    assignments assignments_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.assignments DROP CONSTRAINT assignments_pkey;
       public            postgres    false    222            �           2606    33950    members members_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.members
    ADD CONSTRAINT members_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.members DROP CONSTRAINT members_pkey;
       public            postgres    false    220            �           2606    33990    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            postgres    false    225            �           2606    33976    reports reports_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (request_id);
 >   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_pkey;
       public            postgres    false    223            �           2606    33938 (   request_processes request_processes_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.request_processes
    ADD CONSTRAINT request_processes_pkey PRIMARY KEY (request_id);
 R   ALTER TABLE ONLY public.request_processes DROP CONSTRAINT request_processes_pkey;
       public            postgres    false    218            �           2606    33928    request_regs request_regs_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.request_regs
    ADD CONSTRAINT request_regs_pkey PRIMARY KEY (request_id);
 H   ALTER TABLE ONLY public.request_regs DROP CONSTRAINT request_regs_pkey;
       public            postgres    false    217            �           2606    33923    requests requests_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.requests DROP CONSTRAINT requests_pkey;
       public            postgres    false    216            �           2606    33959 1   assignments unique_assignment_request_responsible 
   CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT unique_assignment_request_responsible UNIQUE (id_request, is_responsible);
 [   ALTER TABLE ONLY public.assignments DROP CONSTRAINT unique_assignment_request_responsible;
       public            postgres    false    222    222            �           2606    33965 #   assignments fk_assignment_member_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fk_assignment_member_id FOREIGN KEY (member_id) REFERENCES public.members(id);
 M   ALTER TABLE ONLY public.assignments DROP CONSTRAINT fk_assignment_member_id;
       public          postgres    false    222    220    3212            �           2606    33960 $   assignments fk_assignment_request_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fk_assignment_request_id FOREIGN KEY (id_request) REFERENCES public.request_processes(request_id);
 N   ALTER TABLE ONLY public.assignments DROP CONSTRAINT fk_assignment_request_id;
       public          postgres    false    222    218    3210            �           2606    33991    orders orders_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.request_processes(request_id);
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_request_id_fkey;
       public          postgres    false    3210    225    218            �           2606    33977    reports reports_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.request_processes(request_id);
 I   ALTER TABLE ONLY public.reports DROP CONSTRAINT reports_request_id_fkey;
       public          postgres    false    3210    218    223            �           2606    33939 3   request_processes request_processes_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.request_processes
    ADD CONSTRAINT request_processes_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 ]   ALTER TABLE ONLY public.request_processes DROP CONSTRAINT request_processes_request_id_fkey;
       public          postgres    false    216    218    3206            �           2606    33929 )   request_regs request_regs_request_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.request_regs
    ADD CONSTRAINT request_regs_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id);
 S   ALTER TABLE ONLY public.request_regs DROP CONSTRAINT request_regs_request_id_fkey;
       public          postgres    false    216    3206    217            0      x������ � �      .   $  x�]�]J�@��'��
���-Ɨ�h�0ѷZAQQ�;1�ik��3;�ޙI��m&s�9�G�P��U�]���h�H�e&ÑHe&�JGC���ۙ��x����U�{�#�o4��	Se�g�j�a���s�u��'!�L��i��,�::���_Zq���F�n�������L@�L�Bߺ�r�h�9aޛ[�xs�gA�/pj��6�T��W4��i�(;�(/�n�[c��u��~��{2j���[�4�}���J��a�y￲�'J޲OLÜ5Ex��-�:9p�8����x:�      3      x������ � �      1      x������ � �      ,      x������ � �      +   t  x�m�Mj�0F��)��)*�,Y�]z'�n��P�Co`҈�q�3�n�Or�F��7�F2�Ж::�H[Nk���/��η��������p����򛦶9����`�PZZ(�)F�(Y�hhN/z�䲰1�T�s�J���l�*n?�����9��~S�;ern��"5�W�~Px�{�.��#�'P�~I���lX�0�R(%�2�F��4�b����! ��'�.�Աirn�u-�&�V�G	�4�� �4۳�,r����:Eِ�x�	n�A~u��~j�C�U�Bk����Ư��A�/�:�S��љ������bu��M�1��x��0wSxut0����ޘ��e�꧁L�]FZ�</&��.˲?���z      *   E  x�uTIn�@<���cFDj��/��1���>�� H@ѢIK&������#E3i��쮪�� �|1���h<���i�RJe���y��H%���[IK�[�ɤ�B�������ղ��䈧�.�l���.���89./yFj��U�l:��q4�����&��<�`
�xA��]�ՙ��E�T*��_���{ĶA�Kp��į@�pXI�V5E8z4��z�Ƴ�.�,�� M%��]zC���/J��\��I�@��1��I� %���ma��~t�5�`���F~a���;v�<'��,(G;�I�����he��I�����a�;��8c ��(�c�IY�q�(W�
�<�j�(���jG|�N�<��ϥ0��={�� �د@~ �;�ޓ��'!���C��0�#F��}������P�= ��)@M��F���
�S^�t5*���4m�mx��X����;Z�/�Vk��1�Fy�Q/��U�6����IS��rd��I�uym��l�8O]kg�p_�6|�+r��P�Q����`:}�ng=��b���~�]�^��a��;;�     
# <img src="/media/logo.png" width="25px" height="25px" /> PowerMap

<details open>
<summary>English Translation</summary>
 
## Summary

- [Introduction](#introduction)
- [Problem to be Solved](#problem-to-be-solved)
- [Adopted Solution](#adopted-solution)
- [Functional Requirements](#functional-requirements)
- [Technical Resources Used](#technical-resources-used)
- [Final Result](#final-result)

## Introduction
<a id="introduction"></a>

PowerMap is a project developed as part of the Mobile Programming Laboratory course, aimed at exploring and applying the concepts learned in the course. This project represents an initiative to understand and address challenges faced by electric and hybrid vehicle drivers, particularly related to charging infrastructure.

During the development of this application, various mobile-specific techniques and technologies were employed to create an effective and intuitive solution for users. The process involved applying knowledge of interface design, mobile platform programming, and integration with web services.

## Problem to be Solved
<a id="problem-to-be-solved"></a>

The increasing popularity of electric vehicles brings many benefits, such as reduced pollution and decreased reliance on fossil fuels. However, in some areas, there is still a significant lack of charging stations to support electric vehicles. This can lead to concerns about vehicle range and difficulties for drivers in finding places to recharge their batteries.

## Adopted Solution
<a id="adopted-solution"></a>

To address this issue, a viable solution is to develop a system that can inform electric and hybrid vehicle drivers about the nearest charging stations and their availability. This system is created as a smartphone app or vehicle navigation system.

The app provides real-time information on the location of nearby charging stations. Additionally, it calculates whether it is possible to reach the desired destination with the current battery charge and vehicle fuel, considering the predicted energy consumption and distance to the destination.

This way, drivers have access to accurate and up-to-date information about charging stations, which facilitates travel planning and reduces range anxiety. Furthermore, this system can encourage the expansion of charging infrastructure, as data on charging demand is more readily accessible to authorities and companies responsible for installing and maintaining charging stations.

## Functional Requirements
<a id="functional-requirements"></a>

You can view the [requirements document (PDF)](/media/DOCUMENTO_DE_REQUISITOS_Power_MAP.docx.pdf), but briefly, the functional requirements of the app are:

```markdown
 [RF001] Register
 [RF002] Log in
 [RF003] Log out
 [RF004] Edit Profile
 [RF005] Change Password
 [RF006] Add Admin
 [RF007] Delete Account
 [RF008] View Users
 [RF009] Create Appointment
 [RF010] Mark Appointment as Done
 [RF011] View Appointment
 [RF012] Use Map
 [RF013] Calculate Cost
 [RF014] Identify Nearest Station
 [RF015] Add Model
 [RF016] View Model
 [RF017] Edit Models
 [RF018] Delete Model
 [RF019] Add Car
 [RF020] View Car
 [RF021] Delete Car
 [RF022] Notify Schedule
```

## Technical Resources Used
<a id="technical-resources-used"></a>

- Java
- Firebase
- Firebase Storage
- SQL Lite
- APIs
- JSON
- Shared Preferences
- Material Design
- Bumptech

## Final Result  
<a id="final-result"></a>

<div style="display: flex">
  <img src="/media/1.jpeg" width="200px" height="360px" />    
   <img src="/media/2.jpeg" width="200px" height="360px"  />    
  <img src="/media/3.jpeg"  width="200px"  height="360px"  />    
  <img src="/media/4.jpeg" width="200px"   height="360px"  />    
  <img src="/media/9.jpeg" width="200px"   height="360px"  />    
  <img src="/media/6.jpeg" width="200px"  height="360px"  />    
  <img src="/media/7.jpeg" width="200px"   height="360px"  />    
  <img src="/media/8.jpeg" width="200px"   height="360px"  />    
</div>
</details>

<details>
<summary>Tradução em Português</summary>
 
## Sumário

- [Introdução](#introdução)
- [Problema a ser resolvido](#problema-a-ser-resolvido)
- [Solução adotada](#solução-adotada)
- [Requisitos Funcionais](#requisitos-funcionais)
- [Recursos Técnicos Utilizados](#recursos-técnicos-utilizados)
- [Resultado Final](#resultado-final)

## Introdução
<a id="introdução"></a>

O PowerMap é um projeto desenvolvido como parte da disciplina de Laboratório de Programação para Dispositivos Móveis, com o objetivo de explorar e aplicar os conceitos aprendidos nessa disciplina. Este projeto representa uma iniciativa para entender e solucionar desafios enfrentados pelos motoristas de veículos elétricos e híbridos, especialmente relacionados à infraestrutura de carregamento.

Durante o desenvolvimento deste aplicativo, foram utilizadas diversas técnicas e tecnologias específicas para dispositivos móveis, visando criar uma solução eficaz e intuitiva para os usuários. Ao longo do processo, foram aplicados conhecimentos sobre design de interfaces, programação em plataformas móveis e integração com serviços web.

## Problema a ser resolvido
<a id="problema-a-ser-resolvido"></a>

A constante popularização dos carros elétricos traz consigo uma série de benefícios, como a redução da poluição e a diminuição da dependência de combustíveis fósseis. No entanto, em determinadas regiões, ainda há uma carência significativa de postos de carregamento que dão suporte a veículos elétricos. Isso pode causar preocupações quanto à autonomia dos veículos e dificuldades para os motoristas encontrarem locais para recarregar suas baterias.

## Solução adotada
<a id="solução-adotada"></a>

Para resolver esse problema, uma solução viável é o desenvolvimento de um sistema que seja capaz de informar os motoristas de carros elétricos e híbridos sobre os postos de carregamento mais próximos e sua disponibilidade. Esse sistema é criado como um aplicativo para smartphones ou sistemas de navegação veicular.

Este aplicativo fornece informações em tempo real sobre a localização dos postos de carregamento mais próximos. Além disso, o sistema calcula se é possível chegar ao destino desejado com a carga atual da bateria e combustível do veículo, levando em consideração o consumo de energia previsto e a distância até o destino.

Dessa forma, os motoristas têm acesso a informações precisas e atualizadas sobre os postos de carregamento, o que facilita o planejamento de suas viagens e reduz a ansiedade relacionada à autonomia dos veículos elétricos. Além disso, esse sistema pode incentivar a expansão da infraestrutura de carregamento, uma vez que os dados sobre a demanda por recarga são mais facilmente acessíveis às autoridades e empresas responsáveis pela instalação e manutenção dos postos de carregamento.

## Requisitos Funcionais
<a id="requisitos-funcionais"></a>

É possível visualizar o [documento de requisitos (PDF)](/media/DOCUMENTO_DE_REQUISITOS_Power_MAP.docx.pdf), mas de forma resumida, os requisitos funcionais do aplicativo são os seguintes:

```markdown
 [RF001] Realizar Cadastro
 [RF002] Realizar login
 [RF003] Realizar Logout
 [RF004] Editar Perfil
 [RF005] Alterar senha
 [RF006] Adicionar admin
 [RF007] Deletar Conta
 [RF008] Visualizar usuários
 [RF009] Criar agendamento
 [RF010] Marcar agendamento como feito
 [RF011] Visualizar agendamento
 [RF012] Utilizar mapa
 [RF013] Realizar Cálculo de gasto
 [RF014] Identificar posto mais próximo
 [RF015] Adicionar Modelo
 [RF016] Visualizar Modelo
 [RF017] Editar Modelos
 [RF018] Deletar modelo
 [RF019] Adicionar Carro
 [RF020] Visualizar Carro
 [RF021] Deletar Carro
 [RF022] Notificar agenda
```

## Recursos Técnicos Utilizados
<a id="recursos-técnicos-utilizados"></a>

- Java
- Firebase
- Firebase Storage
- SQL Lite
- APIs
- JSON
- Shared Preferences
- Material Design
- Bumptech

## Resultado Final  
<a id="resultado-final"></a>

<div style="display: flex">
  <img src="/media/1.jpeg" width="200px" height="360px" />    
   <img src="/media/2.jpeg" width="200px" height="360px"  />    
  <img src="/media/3.jpeg"  width="200px"  height="360px"  />    
  <img src="/media/4.jpeg" width="200px"   height="360px"  />    
  <img src="/media/9.jpeg" width="200px"   height="360px"  />    
  <img src="/media/6.jpeg" width="200px"  height="360px"  />    
  <img src="/media/7.jpeg" width="200px"   height="360px"  />    
  <img src="/media/8.jpeg" width="200px"   height="360px"  />    
</div>
</details>

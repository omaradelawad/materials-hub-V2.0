<img width="1787" height="880" alt="ChatGPT Image Jun 29, 2026, 11_34_09 AM" src="https://github.com/user-attachments/assets/6edca523-f90c-4c2e-bcbd-8c9aaa4b627d" />

# 📚 Materials Hub

**Materials Hub** is an open-source educational platform designed to centralize access to university resources, course materials, and seamless file sharing. Built with a focus on high performance, scalability, and modern web architecture. 🚀

---

## ⚡ The Great Refactor: From Python (FastAPI) to Java (Quarkus)

Initially, Materials Hub was bootstrapped using **Python (FastAPI)**. While FastAPI provided rapid development, the project evolved, demanding stricter type safety, enterprise-grade scalability, and ultra-low memory consumption under heavy loads.

To meet these production demands, the entire backend was re-engineered from scratch using **Java (Quarkus)**.

### Why Quarkus? 🔥
* **Supersonic Subatomic Java:** Drastically reduced memory footprint and near-instant boot times.
* **GraalVM Native Compilation:** Compiled into native binaries for blazing-fast execution and deployment.
* **Developer Joy:** Combines the robust, enterprise ecosystem of Java with live-coding (hot-reload) capabilities.
* **Robust Type Safety:** Moving from Python's dynamic nature to Java's strong typing eliminated an entire class of runtime bugs.

### Architecture Comparison

| Feature | Old Architecture (Python) | New Architecture (Java) |
| :--- | :--- | :--- |
| **Framework** | FastAPI | Quarkus 🚀 |
| **Language** | Python | Java |
| **Performance** | High (Async) | Extremely High (Native / Reactive) |
| **Type Safety** | Dynamic / Type Hints | Statically Typed (Compile-time safety) |
| **Build Artifact** | Python Interpreter Dependent | Native Executable (GraalVM) |

---

## 🛠️ Tech Stack

* **Backend:** Java (Quarkus), Hibernate ORM, REST Qute
* **Frontend:** HTMX (for dynamic, SPA-like UX without heavy JS frameworks), TailwindCSS
* **Database:** MySQL
* **Architecture:** Multi-tier, Open-Source Architecture

# Development & Technical Guides

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST Qute ([guide](https://quarkus.io/guides/qute-reference#rest_integration)): Qute integration for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Security JPA ([guide](https://quarkus.io/guides/security-getting-started)): Secure your applications with username/password stored in a database via Jakarta Persistence

## Provided Code

### REST Qute

Create your web page using Quarkus REST and Qute

[Related guide section...](https://quarkus.io/guides/qute#type-safe-templates)
